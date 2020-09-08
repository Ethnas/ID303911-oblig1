package com.erls.innlevering1.domain;

import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.bind.adapter.JsonbAdapter;

/**
 *
 * @author mikael
 */
public class MediaObjectAdapter implements JsonbAdapter<List<Photo>, JsonArray> {
    @Override
    public JsonArray adaptToJson(List<Photo> mos) throws Exception {
        JsonArrayBuilder result = Json.createArrayBuilder();
        mos.forEach(mo -> result.add(mo.getId()));
        return result.build();
    }

    @Override
    public List<Photo> adaptFromJson(JsonArray mediaid) throws Exception {
        return null;
    }
}