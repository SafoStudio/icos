package com.safo.icos.graphql.scalar;

import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import graphql.execution.CoercedVariables;
import lombok.NonNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import graphql.GraphQLContext;


public class DateTimeScalar {
    private DateTimeScalar() {
        /* This utility class should not be instantiated */
    }

    public static final GraphQLScalarType DATE_TIME = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("Date time scalar")
            .coercing(new Coercing<LocalDateTime, String>() {
                @Override
                public String serialize(@NonNull Object dataFetcherResult,
                                        @NonNull GraphQLContext graphQLContext,
                                        @NonNull java.util.Locale locale) {
                    if (dataFetcherResult instanceof LocalDateTime localDateTime) {
                        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    return null;
                }

                @Override
                public LocalDateTime parseValue(@NonNull Object input,
                                                @NonNull GraphQLContext graphQLContext,
                                                @NonNull java.util.Locale locale) {
                    if (input instanceof String stringValue) {
                        return LocalDateTime.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    return null;
                }

                @Override
                public LocalDateTime parseLiteral(@NonNull Value<?> input,
                                                  @NonNull CoercedVariables variables,
                                                  @NonNull GraphQLContext graphQLContext,
                                                  @NonNull java.util.Locale locale) {
                    if (input instanceof StringValue stringValue && stringValue.getValue() != null) {
                        return LocalDateTime.parse(stringValue.getValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    return null;
                }
            })
            .build();
}