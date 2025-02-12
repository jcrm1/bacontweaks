package com.jcrm1.minecraft.bacontweaks.markdown;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.LinkReferenceDefinition;
import org.commonmark.node.ListItem;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ChatVisitor extends AbstractVisitor {
	private final MutableComponent result = Component.empty();
	private MutableComponent current = result;
	
	@Override
	public void visit(Text node) {
		append(Component.literal(node.getLiteral()));
		visitChildren(node);
	}
	
	@Override
	public void visit(Emphasis node) {
		append(Component.empty().setStyle(current.getStyle().withItalic(true)));
		visitChildren(node);
		append(Component.empty().setStyle(current.getStyle().withItalic(false)));
	}
	
	@Override
	public void visit(StrongEmphasis node) {
		boolean underline = node.getOpeningDelimiter().equals("__");
		if (underline) append(Component.empty().setStyle(current.getStyle().withUnderlined(true)));
		else append(Component.empty().setStyle(current.getStyle().withBold(true)));
		visitChildren(node);
		if (underline) append(Component.empty().setStyle(current.getStyle().withUnderlined(false)));
		else append(Component.empty().setStyle(current.getStyle().withBold(false)));
	}
	
	@Override
	public void visit(CustomNode node) {
		if (node instanceof Strikethrough) {
			append(Component.empty().setStyle(current.getStyle().withStrikethrough(true)));
			visitChildren(node);
			append(Component.empty().setStyle(current.getStyle().withStrikethrough(false)));
		} else visitChildren(node);
	}

    @Override
    public void visit(Code code) {
        visitChildren(code);
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        visitChildren(hardLineBreak);
    }

    @Override
    public void visit(HtmlInline htmlInline) {
        visitChildren(htmlInline);
    }

    @Override
    public void visit(Image image) {
        visitChildren(image);
    }

    @Override
    public void visit(Link link) {
        visitChildren(link);
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        visitChildren(softLineBreak);
    }

    @Override
    public void visit(LinkReferenceDefinition linkReferenceDefinition) {
        visitChildren(linkReferenceDefinition);
    }
	
	private void append(MutableComponent component) {
		current.append(component);
		current = component;
	}
	
	public MutableComponent getResult() {
		return result;
	}
}
