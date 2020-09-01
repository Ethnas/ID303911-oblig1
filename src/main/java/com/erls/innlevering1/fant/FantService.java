package com.erls.innlevering1.fant;

import com.erls.innlevering1.auth.Group;
import com.erls.innlevering1.auth.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import net.coobird.thumbnailator.Thumbnails;
import com.erls.innlevering1.auth.AuthenticationService;
import java.math.BigDecimal;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * REST service class to be used by the UI
 * @author Erlend
 */
@Path("fant")
@Stateless
public class FantService {
    @Inject
    AuthenticationService authService;
    
    @Context
    SecurityContext sc;
    
    @PersistenceContext
    EntityManager em;
    
    /**
     * Public method that returns items with photos sold in the shop
     */
    public List<Item> getItems() {
        
    }
    
    /**
     * A registered user may purchase an Item. An email will be sent to the
     * seller if the purchase is successful
     * @param itemid unique id for item 
     * @return result of purchase request
     */
    public Response purchase(Long itemid) {
    
    }
    
    /**
     * A registered user may remove an item and associated photos owned by the
     * calling user. A user with administrator privileges may remove any item
     * and associated photos.
     * 
     * @param itemid unique id for item to be deleted
     * @return result of delete request
     */
    public Response delete(Long itemid) {
        
    }
    
    /**
     * A registered user may add an item and photo(s) to Fant.
     * @param title the title of Item
     * @param description the description of Item
     * @param price the price of Item
     * @param photos one or more photos associated with Item
     * @return result of the request. If successful, the request will include
     * the new unique ids of the Item and associated Photos
     */
    public Response addItem(String title, String description, BigDecimal price,
            FormDataMultiPart photos) {
    
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
    public Response getPhoto(String name, int width) {
        
    }
}
