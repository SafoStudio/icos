package com.safo.icos.graphql.scalar;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UuidScalar {

    public static final GraphQLScalarType UUID_SCALAR = GraphQLScalarType.newScalar()
            .name("UUID")
            .description("UUID scalar")
            .coercing(new Coercing<UUID, String>() {
                @Override
                public String serialize(Object dataFetcherResult) {
                    if (dataFetcherResult instanceof UUID) {
                        return ((UUID) dataFetcherResult).toString();
                    }
                    return null;
                }

                @Override
                public UUID parseValue(Object input) {
                    if (input instanceof String) {
                        return UUID.fromString((String) input);
                    }
                    return null;
                }

                @Override
                public UUID parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        return UUID.fromString(((StringValue) input).getValue());
                    }
                    return null;
                }
            })
            .build();
}