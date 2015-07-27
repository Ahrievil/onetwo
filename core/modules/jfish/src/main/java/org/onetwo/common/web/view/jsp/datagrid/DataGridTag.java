package org.onetwo.common.web.view.jsp.datagrid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import org.onetwo.common.spring.SpringApplication;
import org.onetwo.common.utils.Page;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.common.utils.map.CasualMap;
import org.onetwo.common.web.config.BaseSiteConfig;
import org.onetwo.common.web.preventor.PreventorFactory;
import org.onetwo.common.web.preventor.RequestPreventor;
import org.onetwo.common.web.view.jsp.TagUtils;
import org.onetwo.common.web.view.jsp.grid.BaseGridTag;
import org.onetwo.common.web.view.jsp.grid.GridTagBean;
import org.springframework.beans.PropertyAccessor;
import org.springframework.web.servlet.tags.NestedPathTag;

@SuppressWarnings("serial")
public class DataGridTag extends BaseGridTag<GridTagBean> {
//	private static final String AJAX_POSTFIX = "Ajax";
//	private static final String AJAX_INST_POSTFIX = "AjaxInst";

	public final static String VIEW_JSUI_KEY = "view.jsui";
	
	private String template = "datagrid/grid.jsp";
	private Object dataSource;
	private int colspan = 0;

	private String action;

	private boolean toolbar;
	private String toolbarName = "grid_toolbar";
	private boolean generatedForm = true;
	private boolean pagination = true;
	
	private PaginationType paginationType = PaginationType.link;
	
	private boolean searchForm;
	

	private boolean ajaxSupported = false;
	private String ajaxZoneName;
//	private String ajaxInstName;
	
	private DatagridRenderListener datagridRenderListener;
	private RequestPreventor csrfPreventor = PreventorFactory.getCsrfPreventor();
	
	private boolean buildQueryString = true;
	
	
	private String custombar = "grid_custombar";
	private String customform = "grid_customform";

	private boolean loadmore = false;
	
	
	
	public DataGridTag(){
		this.datagridRenderListener = SpringApplication.getInstance().getBean(DatagridRenderListener.class, false);
	}
	
	@Override
	public GridTagBean createComponent() {
		GridTagBean gbean = new GridTagBean();
		return gbean;
	}

	/*@Override
	public int doStartTag() throws JspException {
		Deque<HtmlElement> tagStack = new ArrayDeque<HtmlElement>(5);
		setTagStack(tagStack);
		return super.doStartTag();
	}*/

	protected String resolveModelAttribute() {
		return component.getName();
	}
	
	
	protected void forSpringFormTag(){
		// Expose the form object name for nested tags...
		String modelAttribute = resolveModelAttribute();
		this.pageContext.setAttribute(org.springframework.web.servlet.tags.form.FormTag.MODEL_ATTRIBUTE_VARIABLE_NAME, modelAttribute, PageContext.REQUEST_SCOPE);
//		this.pageContext.setAttribute(org.springframework.web.servlet.tags.form.FormTag.MODEL_ATTRIBUTE_VARIABLE_NAME, modelAttribute, PageContext.REQUEST_SCOPE);
		this.pageContext.setAttribute(NestedPathTag.NESTED_PATH_VARIABLE_NAME, modelAttribute + PropertyAccessor.NESTED_PROPERTY_SEPARATOR, PageContext.REQUEST_SCOPE);
	}

	protected int startTag()throws JspException {
		forSpringFormTag();
		return EVAL_BODY_BUFFERED;
	}
	
	@Override
	protected int endTag() throws JspException {
		BodyContent bc = this.getBodyContent();
		if(bc!=null){
			this.component.setBodyContent(bc.getString());
		}
		if(this.datagridRenderListener!=null){
			this.datagridRenderListener.prepareRender(this, component);
		}
		
		try {
//			String t = StringUtils.appendEndWith(getTemplate(), ".jsp");
			String t = getThemeSetting().getTagPage(getTemplate());
			renderTemplate(t);
		} finally{
			clearComponentFromRequest(getGridVarName());
			
//			getTagStack().pop();
//			clearTagStackFromRequest();
		}
		return EVAL_PAGE;
	}
	public String getTemplate() {
		return template;
	}
	
	
	protected void populateComponent() throws JspException{
		super.populateComponent();
		component.setColspan(colspan);
		component.setAction(buildActionString());
		
		component.setQueryString(buildQueryString());
		component.setPagination(pagination);
		component.setPaginationType(paginationType);
		component.setGeneratedForm(generatedForm);
		component.setSearchForm(searchForm);
		component.setAjaxSupported(ajaxSupported);
		if(!Page.class.isInstance(dataSource))
			component.setPagination(false);
		component.setPage(TagUtils.toPage(dataSource));
		
		
		ajaxZoneName = getName();// + AJAX_POSTFIX;
//		ajaxInstName = getName() + AJAX_INST_POSTFIX;
		component.setAjaxZoneName(ajaxZoneName);
//		component.setAjaxInstName(ajaxInstName);
		
		component.setCustombar(custombar);
		component.setCustomform(customform);
		component.setToolbar(toolbar);
		component.setToolbarName(toolbarName);

		component.setJsgrid(getThemeSetting().isJsui());
		
		setComponentIntoRequest(getGridVarName(), component);
	}

	

	protected String buildActionString() {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		if(StringUtils.isBlank(action)){
			String surl = TagUtils.getRequestUri(request);
			return surl;
		}
		this.buildQueryString = false;
		if(action.startsWith(":")){
			return TagUtils.parseAction(request, action, this.csrfPreventor);
		}
		return action;
	}
	

	protected String buildQueryString() {
//		if(!buildQueryString || this.paginationType==PaginationType.form)
		if(!buildQueryString)
			return "";
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
//		String qstr = TagUtils.parseQueryString(request, ":params", this.csrfPreventor);
		CasualMap map = null;
		if(this.paginationType==PaginationType.form){
			map = TagUtils.processUrlSymbolAsCasualMap(request, ":qstr", this.csrfPreventor);
		}else{
			map = TagUtils.processUrlSymbolAsCasualMap(request, ":params", this.csrfPreventor);
		}
		String qstr = map==null?"":map.filter(BaseSiteConfig.THEME_JSUI_KEY).toParamString();
		return qstr;
		/*
		String str = request.getQueryString();
		if(StringUtils.isBlank(str))
			return "";
		CasualMap params = new CasualMap(str);
		params.filter("pageNo"); 
		params.filter(this.csrfPreventor.getFieldOfTokenFieldName());
		params.filter(request.getParameter(this.csrfPreventor.getFieldOfTokenFieldName()));
		str = params.toParamString();
		return str;*/
	}
	
	
	public void setTemplate(String template) {
		this.template = template;
	}

	public void setDataSource(Object dataSource) {
		this.dataSource = dataSource;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setToolbar(boolean toolbar) {
		this.toolbar = toolbar;
	}

	public void setPaginationType(String paginationType) {
		this.paginationType = PaginationType.valueOf(paginationType);
	}

	public void setGeneratedForm(boolean generatedForm) {
		this.generatedForm = generatedForm;
	}

	public void setPagination(boolean pagination) {
		this.pagination = pagination;
	}

	public void setSearchForm(boolean searchForm) {
		this.searchForm = searchForm;
	}

	public void setAjaxZoneName(String ajaxZoneName) {
		this.ajaxZoneName = ajaxZoneName;
	}

	public void setAjaxSupported(boolean ajaxSupported) {
		this.ajaxSupported = ajaxSupported;
	}

	public void setCustombar(String custombar) {
		this.custombar = custombar;
	}

	public void setCustomform(String customform) {
		this.customform = customform;
	}

	public void setToolbarName(String toolbarName) {
		this.toolbarName = toolbarName;
	}

	public boolean isLoadmore() {
		return loadmore;
	}

	public void setLoadmore(boolean loadmore) {
		this.loadmore = loadmore;
	}



}