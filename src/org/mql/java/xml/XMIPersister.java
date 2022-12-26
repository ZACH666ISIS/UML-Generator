package org.mql.java.xml;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.mql.java.models.Cardinal;
import org.mql.java.models.ClassModel;
import org.mql.java.models.Relation;
import org.mql.java.parser.RelationParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMIPersister {
	
	private Document document;
	Element root,
			struct;
	
	public XMIPersister(List<ClassModel> classes, List<Relation> relations) {
		this("UML_Diagram",classes,relations);

	}
	public XMIPersister(String project,List<ClassModel> classes, List<Relation> relations) {
		init(project);
		fillPackages(classes);
		fillRelations(relations);
		createFile();
	}
	private void init(String project) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder =  factory.newDocumentBuilder();
			document =builder.newDocument();
			root = document.createElement("uml:Model");
			struct = document.createElement("struct");
		} catch (Exception e) {}
	
	}
	
	private void fillPackages(List<ClassModel> classes) {
		for(ClassModel c : classes) {
			root.appendChild(createClass(c));
		}
	}
	
	private void fillRelations(List<Relation> relations) {
		for(Relation r : relations) {
			Element relation = document.createElement("ownedAttribute");
			relation.setAttribute("xmi:type", "uml:Association");
			relation.setAttribute("type", r.getType().name());
			relation.appendChild(createClassRelation(r.getFirst(),r.getFirstCard()));
			relation.appendChild(createClassRelation(r.getSecond(),r.getSecondCard()));
			root.appendChild(relation);
		}
		
	}
	
	private Element createClassRelation(ClassModel c , Cardinal card) {
		Element cls = document.createElement("class");
		cls.setAttribute("name", c.getName());
		cls.setAttribute("card", card.name());
		return cls;
	}
	public void createFile() {
		try {
			root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xmi","http://www.omg.org/XMI");
			root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:uml","http://www.eclipse.org/uml2/1.0.0/UML");
			document.appendChild(root);
			Result res = new StreamResult(new File("resources/XMIdiagram.xml"));
			DOMSource source = new DOMSource(document);
			TransformerFactory trsFormFactory = TransformerFactory.newInstance();
			Transformer transformer = trsFormFactory.newTransformer();
			transformer.transform(source, res);
		} catch (Exception e) {}
		
	}

	public Element createClass(ClassModel c) {
		Element pkg = createPackage(c.getClassPackage().getName());
		Element cls = document.createElement("ownedMember");
		cls.setAttribute("xmi:id", String.valueOf(c.getId()));
		cls.setAttribute("name",c.getSimpleName());
		cls.setAttribute("xmi:type", c.getType().name());
		for(Field f : c.getProperties()) {
			cls.appendChild(fillClasses(f));
		}
		pkg.appendChild(cls);
		return pkg;

	}
	
	
	public Element createElm(Object o) {
		Class c = o.getClass();
		return null;
	}
	
	private Element fillField(Field[] fields) {
		Element e = document.createElement("ownedAttribute");
		for(Field f : fields) {
			
			
		}
		
		return null;
	}
	
	private Element fillClasses(Field f) {
		Element field = document.createElement("ownedAttribute");
		field.setAttribute("xmi:id", String.valueOf(UUID.randomUUID()));
		field.setAttribute("xmi:idRef","" );
		field.setAttribute("name", f.getName());
		field.setAttribute("type", f.getGenericType().getTypeName());
		field.setAttribute("visibility", Modifier.toString(f.getModifiers()));	
		return field;
	}
	
	
	
	public Element createPackage(String name) {
		
		if(searchPackage(name) == null) {
			Element pkg = document.createElement("uml:Package");
			pkg.setAttribute("xmi:id", String.valueOf(name.hashCode()));
			pkg.setAttribute("name", name);
			root.appendChild(pkg);
			return pkg;
		}
		return searchPackage(name);
	}
	
	public Element searchPackage(String name) {
		NodeList list = root.getChildNodes();
		for(int i=0;i<list.getLength();i++) {
			if(list.item(i).getNodeName().equals("uml:Package"))
				if(attribute(list.item(i),"xmi:id").equals(String.valueOf(name.hashCode())))
					return (Element) list.item(i);
		}
		return null;
	}
	
	public String attribute(Node node,String name) {
		NamedNodeMap atts =node.getAttributes();
		return atts.getNamedItem(name).getNodeValue();
	}
	

	


}