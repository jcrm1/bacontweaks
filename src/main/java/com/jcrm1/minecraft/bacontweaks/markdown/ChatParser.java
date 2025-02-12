package com.jcrm1.minecraft.bacontweaks.markdown;

import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.List;

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

public class ChatParser {
	private static final Parser PARSER = Parser.builder()
			.enabledBlockTypes(new HashSet<>())
			.extensions(List.of(StrikethroughExtension.builder().requireTwoTildes(true).build()))
			.build();
	public static Component parse(String text) {
		ChatVisitor visitor = new ChatVisitor();
		Node root = PARSER.parse(text);
		root.accept(visitor);
		return visitor.getResult();
	}
}
