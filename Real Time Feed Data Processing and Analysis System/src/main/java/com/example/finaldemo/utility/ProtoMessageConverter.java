package com.example.finaldemo.utility;

import com.google.protobuf.util.JsonFormat;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

public class ProtoMessageConverter {
    private final JsonFormat.TypeRegistry typeRegistry;
    private final JsonFormat.Parser parser;
    private final JsonFormat.Printer printer;
    private final ProtobufHttpMessageConverter httpMessageConverter;

    public ProtoMessageConverter(JsonFormat.TypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
        this.parser = JsonFormat.parser()
                .usingTypeRegistry(typeRegistry)
                .ignoringUnknownFields();
        this.printer = JsonFormat.printer()
                .usingTypeRegistry(typeRegistry)
                .preservingProtoFieldNames()
                .includingDefaultValueFields()
                .omittingInsignificantWhitespace();
        this.httpMessageConverter = new ProtobufJsonFormatHttpMessageConverter(parser, this.printer);
    }

    public JsonFormat.TypeRegistry getTypeRegistry() {
        return typeRegistry;
    }

    public JsonFormat.Parser getParser() {
        return parser;
    }

    public JsonFormat.Printer getPrinter() {
        return printer;
    }

    public ProtobufHttpMessageConverter getHttpMessageConverter() {
        return httpMessageConverter;
    }
}