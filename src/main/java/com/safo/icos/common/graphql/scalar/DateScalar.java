package com.safo.icos.common.graphql.scalar;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.NonNull;

public class DateScalar {
  public static final GraphQLScalarType DATE = GraphQLScalarType.newScalar()
          .name("Date")
          .description("Date scalar (ISO-8601)")
          .coercing(new Coercing<LocalDate, String>() {
            @Override
            public String serialize(@NonNull Object dataFetcherResult,
                                    @NonNull GraphQLContext graphQLContext,
                                    @NonNull java.util.Locale locale) {
              if (dataFetcherResult instanceof LocalDate localDate) {
                return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
              }
              return null;
            }

            @Override
            public LocalDate parseValue(@NonNull Object input,
                                        @NonNull GraphQLContext graphQLContext,
                                        @NonNull java.util.Locale locale) {
              if (input instanceof String stringValue) {
                return LocalDate.parse(stringValue, DateTimeFormatter.ISO_LOCAL_DATE);
              }
              return null;
            }

            @Override
            public LocalDate parseLiteral(@NonNull Value<?> input,
                                          @NonNull CoercedVariables variables,
                                          @NonNull GraphQLContext graphQLContext,
                                          @NonNull java.util.Locale locale) {
              if (input instanceof StringValue stringValue && stringValue.getValue() != null) {
                return LocalDate.parse(stringValue.getValue(), DateTimeFormatter.ISO_LOCAL_DATE);
              }
              return null;
            }
          })
          .build();

  private DateScalar() {
    /* This utility class should not be instantiated */
  }
}
