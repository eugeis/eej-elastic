package ee.elastic.ui.html;

public interface Generator {
	TemplateFactory templates();

	StringBuffer generate(Element root);

	Generator templates(TemplateFactory templates);
}
