package com.erls.innlevering1.fant;

import com.erls.innlevering1.auth.Group;
import com.erls.innlevering1.auth.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;

import com.erls.innlevering1.response.ErrorMessage;
import com.erls.innlevering1.response.ErrorResponse;
import net.coobird.thumbnailator.Thumbnails;
import com.erls.innlevering1.auth.AuthenticationService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import com.erls.innlevering1.domain.Item;
import com.erls.innlevering1.domain.Photo;
import com.erls.innlevering1.mail.JavaxMail;
import com.erls.innlevering1.response.DataResponse;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.activation.DataHandler;
import javax.persistence.TypedQuery;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.jwt.JsonWebToken;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.media.multipart.BodyPart;


/**
 * REST service class to be used by the UI
 * @author Erlend
 */
@Path("/fant")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FantService {
    
    private static final String ADMIN_EMAIL = "admin@fant.no";
    
    @Inject
    @ConfigProperty(name = "photo.storage.path", defaultValue = "fantphotos")
    String photoPath;
    
    private final String PHOTO_PATH = "photos/items/";
    
    @Inject
    IdentityStoreHandler identityStoreHandler;
    
    @Inject
    @ConfigProperty(name = "mp.jwr.verify.issuer", defaultValue = "issuer")
    String issuer;
    
    @Inject
    AuthenticationService authService;
    
    @Inject
    JsonWebToken tk;
    
    @Context
    SecurityContext sc;
    
    @PersistenceContext
    EntityManager em;
    
    /**
     * Public method that returns items with photos sold in the shop
     * @return result of getItem request.
     */
    @GET
    @Path("/getItems")
    public Response getItems(@QueryParam("page") int page) {
        ResponseBuilder resp;
        try {
            Long totalItems = em.createNamedQuery(Item.COUNT_TOTAL_ITEMS, Long.class).getSingleResult();
            int  pageSize = 10;
            int totalPages = (int) Math.ceil(totalItems / pageSize);
            int lower = 0;
            if (page < 1) {
                page = 1;
            }
            if (page >= 1) {
                lower = (page - 1) * pageSize;
                if (lower > totalItems) {
                    lower = totalPages * pageSize;
                }
            }
            TypedQuery<Item> tq = em.createNamedQuery(Item.GET_ALL_DESC, Item.class);
            tq.setMaxResults(pageSize);
            tq.setFirstResult(lower);
            List<Item> items = tq.getResultList();
            resp = Response.ok(new DataResponse(items).getResponse());
        } catch (Exception e) {
            resp = Response.ok(new ErrorResponse(new ErrorMessage("Could not get items"))
                    .getResponse()).status(Status.NOT_FOUND);
        }
        return resp.build();
    }
    
    /**
     * A registered user may purchase an Item. An email will be sent to the
     * seller if the purchase is successful
     * @param itemId unique id for item
     * @return result of purchase request
     */
    @POST
    @Path("/buyitem")
    @RolesAllowed(value = { Group.USER, Group.ADMIN })
    public Response purchaseItem(@HeaderParam("id") Long itemId) {
        ResponseBuilder resp = null;
        User buyer = authService.getCurrentUser();
        Item item = em.find(Item.class, itemId);
        if (item == null) {
            ErrorMessage message = new ErrorMessage("No item");
            resp = Response.ok(new ErrorResponse(message));
        } else if (!(item.getSeller().getUserid().equals(buyer.getUserid()))) {
            item.setBuyer(buyer);
            item.setSold(true);
            em.persist(item);
            JavaxMail mail = new JavaxMail(item.getSeller().getEmail(), ADMIN_EMAIL, 
                    "Item: " + item.getName() + " was sold", "Your item was sold.");
            mail.setHost("fant_mail");
            mail.send();
            resp = Response.ok(new DataResponse("ok"));
        } else {
            ErrorMessage message = new ErrorMessage("Can't buy own item");
            resp = Response.ok(new ErrorResponse(message));
        }
        return resp.build();
    }
    
    /**
     * A registered user may remove an item and associated photos owned by the
     * calling user. A user with administrator privileges may remove any item
     * and associated photos.
     * 
     * @param itemid unique id for item to be deleted
     * @return result of delete request
     */
    @DELETE
    @Path("/deleteitem")
    @RolesAllowed(value = { Group.USER, Group.ADMIN })
    public Response deleteItem(@HeaderParam("itemId") Long itemid) {
        ResponseBuilder resp;
        try {
            User user = authService.getCurrentUser();
            int updated = em.createNamedQuery(Item.DELETE_BY_ID, Item.class)
                    .setParameter("id", itemid).setParameter("seller", user)
                    .executeUpdate();
            if (updated > 0) {
                resp = Response.ok(new DataResponse("").getResponse());
            } else {
                resp = Response.ok(new ErrorResponse(new ErrorMessage("No items deleted")).getResponse());
            }
        } catch (Exception e) {
            resp = Response.ok(new ErrorResponse(
                    new ErrorMessage("Something went wrong with deleting item with id " + itemid))
                    .getResponse());
        }
        return resp.build();
    }
    
    /**
     * A registered user may add an item and photo(s) to Fant.
     * @param title the title of Item
     * @param description the description of Item
     * @param price the price of Item
     * @param multiPartData one or more photos associated with Item
     * @param request the http request
     * @return result of the request. If successful, the request will include
     * the new unique ids of the Item and associated Photos
     */
    @POST
    @Path("/additem")
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON })
    @RolesAllowed({ Group.USER, Group.ADMIN })
    public Response addItem(@HeaderParam("title") String title, @HeaderParam("description") String description, 
            @HeaderParam("price") float price, FormDataMultiPart multiPartData, @Context HttpServletRequest request) {
        ResponseBuilder resp;
        try {
            User user = authService.getCurrentUser();
            Item item = new Item(title, description, price, user);
            Set<Photo> photos = saveImages(multiPartData);
            for (Photo photo : photos) {
                photo.setOwner(item);
            }
            item.setPhoto(photos);
            em.persist(item);
            resp = Response.ok(new DataResponse().getResponse());
        } catch (Exception e) {
            resp = Response.ok(new ErrorResponse(new ErrorMessage("Could not store item")).getResponse());
        }
        
        return resp.build();
    }
    
    private Set<Photo> saveImages(FormDataMultiPart multiPartData) {

		List<BodyPart> attachments = multiPartData.getBodyParts();
		InputStream stream = null;
		Set<Photo> photos = new HashSet<>();

		File directory = new File(PHOTO_PATH);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		for (Iterator<BodyPart> it = attachments.iterator(); it.hasNext();) {
			try {
				BodyPart attachment = it.next();
				if (attachment == null) {
					continue;
				}
				DataHandler dataHandler = attachment.getEntityAs(DataHandler.class);
				stream = dataHandler.getInputStream();

				MultivaluedMap<String, String> map = attachment.getHeaders();

				String fileName = null;
				String formElementName = null;
				String[] contentDisposition = map.getFirst("Content-Disposition").split(";");

				for (String tempName : contentDisposition) {
					try {
						String[] names = tempName.split("=");
						formElementName = names[1].trim().replaceAll("\"", "");
						if ((tempName.trim().startsWith("filename"))) {
							fileName = formElementName;
						}
					} catch (Exception e) {
						continue;
					}
				}
				if (fileName != null) {
					String pid = UUID.randomUUID().toString();
					String newName = pid + "-" + fileName.trim();
					String fullFileName = PHOTO_PATH + newName;
					MediaType mediatype = attachment.getEntityAs(MediaType.class);
                                        Photo photo = new Photo();

					long size = Files.copy(stream, Paths.get(fullFileName));
					photo.setName(newName);
					photo.setMimeType(mediatype.toString());
					photo.setFileSize(size);
					photos.add(photo);

				}
				if (stream != null) {
					System.out.println("Closing stream");
					stream.close();
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return photos;
	}
    
    /**
	 * Returns an item with given id, if it is found. Else return nothing, with
	 * error.
	 * 
	 * @param id the id of the item
	 * @return return Response
	 */
	@GET
	@Path("/getitem")
	public Response getItem(@QueryParam("id") Integer id) {
		ResponseBuilder resp;
		Item item = em.find(Item.class, Long.valueOf(id));
		if (item == null) {
			resp = Response.ok(new ErrorResponse(new ErrorMessage("No items with id " + id)).getResponse());
		} else {
			resp = Response.ok(new DataResponse(item).getResponse());
		}
		return resp.build();
	}
        
        
    private String getPhotoPath() {
        return photoPath;
    }
    
    /**
     * Streams an image to the browser (the actual compressed pixels). The image
     * will be scaled to the appropriate width if the width parameter is provided.
     * This is a public method available to all callers.
     * 
     * @param name the filename of the image
     * @param width the required scaled with of the image
     * 
     * @return the image in original format or in jpeg if scaled
     */
    @GET
    @Path("/image/{name}")
    @Produces("image/jpeg")
    public Response getPhoto(@PathParam("name") String name, 
            @PathParam("width") int width) {
        if(em.find(Photo.class, name) != null) {
            StreamingOutput result = (OutputStream os) -> {
                java.nio.file.Path image = Paths.get(getPhotoPath(),name);
                if(width == 0) {
                    Files.copy(image, os);
                    os.flush();
                } else {
                    Thumbnails.of(image.toFile())
                              .size(width, width)
                              .outputFormat("jpeg")
                              .toOutputStream(os);
                }
            };

            // Ask the browser to cache the image for 24 hours
            CacheControl cc = new CacheControl();
            cc.setMaxAge(86400);
            cc.setPrivate(true);

            return Response.ok(result).cacheControl(cc).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
