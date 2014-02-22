package org.onetwo.common.excel.view.jsp;

import org.onetwo.common.excel.ExcelUtils;
import org.onetwo.common.excel.FieldModel;
import org.onetwo.common.excel.RowModel;
import org.onetwo.common.excel.TemplateModel;
import org.onetwo.common.excel.WorkbookModel;
import org.onetwo.common.jackson.JsonMapper;
import org.onetwo.common.web.view.jsp.datagrid.AbstractDatagridRenderListener;
import org.onetwo.common.web.view.jsp.datagrid.DataGridTag;
import org.onetwo.common.web.view.jsp.grid.FieldTagBean;
import org.onetwo.common.web.view.jsp.grid.GridTagBean;
import org.onetwo.common.web.view.jsp.grid.RowTagBean;

public class DatagridExcelModelBuilder extends AbstractDatagridRenderListener {

	@Override
	public void prepareRender(DataGridTag tag, GridTagBean tagb) {
		ExportableGridTagBean tagBean = (ExportableGridTagBean) tagb;
		if(!tagBean.isExportable())
			return ;
		WorkbookModel workbook = new WorkbookModel();
		TemplateModel template = new TemplateModel();
		template.setLabel(tagBean.getTitle());
		template.setName(tagBean.getName());
		for(RowTagBean rowTag : tagBean.getRows()){
			RowModel row = buildRow(rowTag);
			if(row.isIterator()){
				row.setDatasource(tagBean.getExportDataSource());
			}
			template.addRow(row);
		}
		workbook.addSheet(template);
		JsonMapper jsonMapper = JsonMapper.ignoreEmpty()
											.filter(ExcelUtils.JSON_FILTER_TEMPLATE, "multiSheet", "varName")
											.filter(ExcelUtils.JSON_FILTER_ROW, "row", "title", "space", "span", "height")
											.filter(ExcelUtils.JSON_FILTER_FIELD, "space", "height", "columnTotal", "rowTotal", "var", "rowField", "range", "colspan", "rowspan");
		String json = jsonMapper.toJson(workbook);
//		tag.write("<input name=exporter type=hidden value='"+json+"'/>");
		tagBean.setExportJsonTemplate(json);
	}
	
	private RowModel buildRow(RowTagBean rowTag){
		RowModel row = new RowModel();
		row.setType(rowTag.getType().toString());
		row.setName(rowTag.getName());
		row.setRenderHeader(rowTag.isRenderHeader());
		for(FieldTagBean ft : rowTag.getFields()){
			ExportableFieldTagBean fieldTag = (ExportableFieldTagBean) ft;
			if(fieldTag.isExportable())
				row.addField(buildField(fieldTag));
		}
		return row;
	}
	
	private FieldModel buildField(FieldTagBean fieldTag){
		FieldModel field = new FieldModel();
		field.setName(fieldTag.getName());
		field.setValue(fieldTag.getValue());
		field.setLabel(fieldTag.getLabel());
		return field;
	}
	

}