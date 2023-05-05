package db;

import beans.Product;
import com.fasterxml.jackson.databind.JsonNode;

public class SearchHandler extends DBtask {


    @Override
    public JsonNode doExecute(JsonNode node) {

        /*unmarshal
                prata med db
                få tillbaka
                        marshall
                                return node;*/
        return null;
    }

    public Product[] search(Product product) {

    }
}

