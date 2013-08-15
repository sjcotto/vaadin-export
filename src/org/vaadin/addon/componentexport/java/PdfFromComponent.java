package org.vaadin.addon.componentexport.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.Application;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class PdfFromComponent {

	private String titulo;
	private File tempFile;
	private String target = "_self";
	private String fileName;

	
	public void export(Component c) {
		
		List<Component> list = new ArrayList<Component>();
		list.add(c);
		this.export(list);
		
	}
	public void export(List<Component> list) {

		Document document = new Document();

		tempFile = null;
		FileOutputStream fileOut = null;
		try {
			tempFile = File.createTempFile("tmp", ".pdf");
			fileOut = new FileOutputStream(tempFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PdfWriter.getInstance(document, fileOut).setInitialLeading(20);
		} catch (DocumentException e) {

			e.printStackTrace();
		}

		
		
		

		if (list == null || list.isEmpty()) {
			return;
		}

		Application app = list.get(0).getApplication();
		document.open();

		try {

			if (titulo != null) {

				addTitle(document, titulo);

			}

			document.add(Chunk.NEWLINE);

			for (Component c : list) {
				// dependiendo del tipo de elemento lo ingresamos en el pdf
				ingresarComponente(document, c);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		document.close();

		TemporaryFileDownloadResource resource;
		try {
			resource = new TemporaryFileDownloadResource(app, "export.pdf",
					"aplication/pdf", this.tempFile);

			app.getMainWindow().open(resource, target);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void addTitle(Document document, String titulo2) throws Exception {

		document.add(new Paragraph(
				titulo,
				FontFactory
						.getFont(
								"'Segoe UI Light','Open Sans',Verdana,Arial,Helvetica,sans-serif",
								22, Font.ITALIC, BaseColor.BLUE)));

	}

	private void ingresarLayout(Document document, AbstractLayout h) {

		Iterator<Component> it = h.getComponentIterator();

		while (it.hasNext()) {
			Component c = it.next();
			ingresarComponente(document, c);
		}

	}

	private void ingresarComponente(Document document, Component c) {

		try {
			if (c instanceof TextField) {
				TextField tF = (TextField) c;
				ingresarTextField(document, tF);
				document.add(Chunk.NEWLINE);
			} else if (c instanceof Table) {
				Table t = (Table) c;
				ingresarTabla(document, t);
				document.add(Chunk.NEWLINE);
			} else if (c instanceof TextArea) {
				TextArea tA = (TextArea) c;
				ingresarTextArea(document, tA);
				document.add(Chunk.NEWLINE);
			} else if (c instanceof Select) {
				Select s = (Select) c;
				ingresarSelect(document, s);
				document.add(Chunk.NEWLINE);
			} else if (c instanceof HorizontalLayout) {
				HorizontalLayout ho = (HorizontalLayout) c;
				ingresarLayout(document, ho);
			} else if (c instanceof VerticalLayout) {
				VerticalLayout ho = (VerticalLayout) c;
				ingresarLayout(document, ho);
			} else if (c instanceof Form) {
				Form ho = (Form) c;
				ingresarForm(document, ho);
			}else if (c instanceof Label) {
				Label tF = (Label) c;
				ingresarLabel(document, tF);
				document.add(Chunk.NEWLINE);
			}else if (c instanceof Field) {{
				Field tF = (Field) c;
				addBoldText(document, tF.getCaption());
				addNormalText(document, tF.getValue().toString());
			}
				
			}

		} catch (Exception e) {

			this.addNormalText(document, "N/A");
			try {
				document.add(Chunk.NEWLINE);
			} catch (DocumentException e1) {
			}
			catch (Exception e1) {
				//nada
			}
		}
	}

	private void ingresarLabel(Document document, Label s) {
		addBoldText(document, s.getCaption());
		addNormalText(document, s.getValue().toString());
		
	}
	private void ingresarForm(Document document, Form ho) {

		// TODO ver si esta bien
		for (Object o : ho.getItemPropertyIds()) {
			Component c = ho.getField(o);
			this.ingresarComponente(document, c);
		}
	}

	private void ingresarTabla(Document document, Table t) {

		if (t.getCaption() != null) {
			addBoldText(document, t.getCaption());
		}

		Collection<Object> inputItemIds = (Collection<Object>) t
				.getContainerDataSource().getItemIds();

		Container container = t.getContainerDataSource();
		Collection<?> prop = container.getContainerPropertyIds();
		PdfPTable table = new PdfPTable(prop.size());

		for (Object obj : t.getVisibleColumns()) {
			try {
				String str = (String) obj;
				if (str != null && !str.equals("null")) {
					table.addCell(str);
				}
			} catch (Exception e) {
				// si da error lo dejamos pasar (tipo de datos)
			}

		}

		for (Object itemId : inputItemIds) {
			for (Object obj : prop) {
				Property value = container.getContainerProperty(itemId, obj);
				table.addCell(value.getValue().toString());
			}
		}
		try {
			document.add(table);
		} catch (DocumentException e) {

			e.printStackTrace();
		}

	}

	private void ingresarSelect(Document document, Select s) {

		addBoldText(document, s.getCaption());
		addNormalText(document, s.getValue().toString());

	}

	private void ingresarTextArea(Document document, TextArea tA) {

		addBoldText(document, tA.getCaption());

		addNormalText(document, tA.getValue().toString());
	}

	private void ingresarTextField(Document document, TextField tF) {

		addBoldText(document, tF.getCaption());

		addNormalText(document, tF.getValue().toString());

	}

	private void addNormalText(Document document, String string) {

		try {
			document.add(new Paragraph(
					string,
					FontFactory
							.getFont(
									"'Segoe UI Light','Open Sans',Verdana,Arial,Helvetica,sans-serif",
									11, Font.NORMAL, BaseColor.BLACK)));
		} catch (DocumentException e) {

		}

	}

	private void addBoldText(Document document, String caption) {

		try {
			document.add(new Paragraph(
					caption,
					FontFactory
							.getFont(
									"'Segoe UI Light','Open Sans',Verdana,Arial,Helvetica,sans-serif",
									12, Font.BOLD, BaseColor.BLACK)));
		} catch (DocumentException e) {

		}

	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo
	 *            the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * @return the tempFile
	 */
	public File getTempFile() {
		return tempFile;
	}

	/**
	 * @param tempFile
	 *            the tempFile to set
	 */
	public void setTempFile(File tempFile) {
		this.tempFile = tempFile;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
