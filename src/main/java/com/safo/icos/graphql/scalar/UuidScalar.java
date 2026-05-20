package com.safo.icos.graphql.scalar;

import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import graphql.execution.CoercedVariables;
import lombok.NonNull;
import graphql.GraphQLContext;
import java.util.UUID;

public class UuidScalar {

    private UuidScalar() {
        /* This utility class should not be instantiated */
    }

    public static final GraphQLScalarType UUID_SCALAR = GraphQLScalarType.newScalar()
            .name("UUID")
            .description("UUID scalar")
            .coercing(new Coercing<UUID, String>() {
                @Override
                public String serialize(@NonNull Object dataFetcherResult,
                                        @NonNull GraphQLContext graphQLContext,
                                        @NonNull java.util.Locale locale) {
                    if (dataFetcherResult instanceof UUID uuid) {
                        return uuid.toString();
                    }
                    return null;
                }

                @Override
                public UUID parseValue(@NonNull Object input,
                                       @NonNull GraphQLContext graphQLContext,
                                       @NonNull java.util.Locale locale) {
                    if (input instanceof String stringValue) {
                        return UUID.fromString(stringValue);
                    }
                    return null;
                }

                @Override
                public UUID parseLiteral(@NonNull Value<?> input,
                                         @NonNull CoercedVariables variables,
                                         @NonNull GraphQLContext graphQLContext,
                                         @NonNull java.util.Locale locale) {
                    if (input instanceof StringValue stringValue && stringValue.getValue() != null) {
                        return UUID.fromString(stringValue.getValue());
                    }
                    return null;
                }
            })
            .build();
}