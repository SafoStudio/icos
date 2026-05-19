package com.safo.icos.graphql.scalar;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeScalar {

    public static final GraphQLScalarType DATE_TIME = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("Date time scalar")
            .coercing(new Coercing<LocalDateTime, String>() {
                @Override
                public String serialize(Object dataFetcherResult) {
                    if (dataFetcherResult instanceof LocalDateTime) {
                        return ((LocalDateTime) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    return null;
                }

                @Override
                public LocalDateTime parseValue(Object input) {
                    if (input instanceof String) {
                        return LocalDateTime.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    return null;
                }

                @Override
                public LocalDateTime parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        return LocalDateTime.parse(((StringValue) input).getValue(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    return null;
                }
            })
            .build();
}