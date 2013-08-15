package org.vaadin.addon.componentexport;

import org.vaadin.addon.componentexport.java.PdfFromComponent;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.ui.*;

public class ComponentexportdemoApplication extends Application {
	private VerticalLayout all;

	@Override
	public void init() {
		Window mainWindow = new Window("Componentexportdemo Application");
		
		
		
		all = new VerticalLayout();
		Label label = new Label("this is a label");
		
		/* Create the table with a caption. */
		Table table = new Table("This is a Table");

		/* Define the names and data types of columns.
		 * The "default value" parameter is meaningless here. */
		table.addContainerProperty("First Name", String.class,  null);
		table.addContainerProperty("Last Name",  String.class,  null);
		table.addContainerProperty("Year",       Integer.class, null);

		/* Add a few items in the table. */
		table.addItem(new Object[] {
		    "Nicolaus","Copernicus",new Integer(1473)}, new Integer(1));
		table.addItem(new Object[] {
		    "Tycho",   "Brahe",     new Integer(1546)}, new Integer(2));
		table.addItem(new Object[] {
		    "Giordano","Bruno",     new Integer(1548)}, new Integer(3));
		table.addItem(new Object[] {
		    "Galileo", "Galilei",   new Integer(1564)}, new Integer(4));
		table.addItem(new Object[] {
		    "Johannes","Kepler",    new Integer(1571)}, new Integer(5));
		table.addItem(new Object[] {
		    "Isaac",   "Newton",    new Integer(1643)}, new Integer(6));
        
		Select s = new Select("select");
		for (int i=0;i<50;i++){
			s.addItem("Item nro: "+i);
		}
		
		TextField t = new TextField("this is a textfield");
		t.setValue("Hola");
		
		TextArea t2 = new TextArea("this is a textarea");
		t.setValue("areaaaaaaaaaaaaaa");
		
		Button b = new Button("PDF");
		
		b.addListener(new com.vaadin.ui.Button.ClickListener() {
			
			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				PdfFromComponent factory = new PdfFromComponent();
				factory.export(all);
				
			}
		});

		
		all.addComponent(label);
		all.addComponent(table);
		all.addComponent(s);
		all.addComponent(t);
		all.addComponent(t2);
		all.addComponent(b);
		
		all.setWidth("500px");
		mainWindow.addComponent(all);
		setMainWindow(mainWindow);
	}

}
