package com.logicaldoc.gui.common.client.util;

import java.util.Date;
import java.util.LinkedHashMap;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.InputValues;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.beans.GUIAttribute;
import com.logicaldoc.gui.common.client.beans.GUIAttributeSet;
import com.logicaldoc.gui.common.client.beans.GUIImportFolder;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIRetentionPolicy;
import com.logicaldoc.gui.common.client.data.ArchivesDS;
import com.logicaldoc.gui.common.client.data.AttributeOptionsDS;
import com.logicaldoc.gui.common.client.data.AttributeSetsDS;
import com.logicaldoc.gui.common.client.data.AttributesDS;
import com.logicaldoc.gui.common.client.data.CharsetsDS;
import com.logicaldoc.gui.common.client.data.ContactsDS;
import com.logicaldoc.gui.common.client.data.ConversionFormatsDS;
import com.logicaldoc.gui.common.client.data.EventsDS;
import com.logicaldoc.gui.common.client.data.FolderTemplatesDS;
import com.logicaldoc.gui.common.client.data.FoldersDS;
import com.logicaldoc.gui.common.client.data.FormatConvertersDS;
import com.logicaldoc.gui.common.client.data.FormsDS;
import com.logicaldoc.gui.common.client.data.GroupsDS;
import com.logicaldoc.gui.common.client.data.SkinsDS;
import com.logicaldoc.gui.common.client.data.StampsDS;
import com.logicaldoc.gui.common.client.data.StoragesDS;
import com.logicaldoc.gui.common.client.data.StoragesTypesDS;
import com.logicaldoc.gui.common.client.data.TagsDS;
import com.logicaldoc.gui.common.client.data.TemplatesDS;
import com.logicaldoc.gui.common.client.data.TenantsDS;
import com.logicaldoc.gui.common.client.data.UsersDS;
import com.logicaldoc.gui.common.client.data.WorkflowsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.validators.EmailValidator;
import com.logicaldoc.gui.common.client.validators.EmailsValidator;
import com.logicaldoc.gui.common.client.validators.SimpleTextValidator;
import com.logicaldoc.gui.common.client.widgets.UserSelector;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.MultiComboBoxLayoutStyle;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.DateChooser;
import com.smartgwt.client.widgets.DateRangeDialog;
import com.smartgwt.client.widgets.HeaderControl.HeaderIcon;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemValueFormatter;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ColorPickerItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.DateRangeItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.MiniDateRangeItem;
import com.smartgwt.client.widgets.form.fields.MultiComboBoxItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.RowSpacerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.TimeItem;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.form.validator.IsFloatValidator;
import com.smartgwt.client.widgets.form.validator.IsIntegerValidator;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Collection of useful factory methods for form items.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 6.0
 */
public class ItemFactory {

	static {
		DateRangeDialog dialog = new DateRangeDialog();
		dialog.setCancelButtonTitle(I18N.message("cancel"));
		dialog.setClearButtonTitle(I18N.message("clear"));
		dialog.setOkButtonTitle(I18N.message("ok"));
		dialog.setHeaderTitle(I18N.message("selectdaterange"));
		dialog.setTitle(I18N.message("selectdaterange"));
		DateRangeDialog.setDefaultProperties(dialog);

		DateRangeItem dateRangeItem = new DateRangeItem();
		dateRangeItem.setFromTitle(I18N.message("from"));
		dateRangeItem.setToTitle(I18N.message("to"));
		dateRangeItem.setDateFormatter(I18N.getDateDisplayFormat(false));
		dateRangeItem.setPickerIconPrompt(I18N.message("showdatechooser"));
		dateRangeItem.setHintStyle("hint");
		dateRangeItem.setRequiredMessage(I18N.message("fieldrequired"));
		DateRangeItem.setDefaultProperties(dateRangeItem);

		MiniDateRangeItem miniDateRangeItem = new MiniDateRangeItem();
		miniDateRangeItem.setShowPickerIcon(true);
		miniDateRangeItem.setAllowRelativeDates(false);
		miniDateRangeItem.setHintStyle("hint");
		miniDateRangeItem.setWidth(180);
		miniDateRangeItem.setDateFormatter(I18N.getDateDisplayFormat(false));
		miniDateRangeItem.setDateDisplayFormat(I18N.getDateDisplayFormat(false));
		miniDateRangeItem.setToDateOnlyPrefix(I18N.message("bbefore"));
		miniDateRangeItem.setFromDateOnlyPrefix(I18N.message("ssince"));
		miniDateRangeItem.setPickerIconPrompt(I18N.message("showdatechooser"));
		miniDateRangeItem.setRequiredMessage(I18N.message("fieldrequired"));
		MiniDateRangeItem.setDefaultProperties(miniDateRangeItem);

		DateItem dateItem = new DateItem();
		dateItem.setUseTextField(true);
		dateItem.setUseMask(true);
		dateItem.setShowPickerIcon(true);
		dateItem.setHintStyle("hint");
		dateItem.setWidth(120);
		dateItem.setDateFormatter(I18N.getDateDisplayFormat(false));
		dateItem.setPickerIconPrompt(I18N.message("showdatechooser"));
		dateItem.setRequiredMessage(I18N.message("fieldrequired"));
		dateItem.setEndDate(new Date(2208985200000L));
		dateItem.setStartDate(new Date(631148400000L));
		DateItem.setDefaultProperties(dateItem);

		DateChooser dateChooser = new DateChooser();
		dateChooser.setEndYear(2040);
		dateChooser.setStartYear(1990);
		DateChooser.setDefaultProperties(dateChooser);

		SelectItem selectItem = new SelectItem();
		selectItem.setWidth(150);
		selectItem.setHintStyle("hint");
		selectItem.setRequiredMessage(I18N.message("fieldrequired"));
		SelectItem.setDefaultProperties(selectItem);

		TextItem textItem = new TextItem();
		textItem.setHintStyle("hint");
		textItem.setWidth(150);
		textItem.setRequiredMessage(I18N.message("fieldrequired"));
		TextItem.setDefaultProperties(textItem);

		RadioGroupItem radioGroupItem = new RadioGroupItem();
		radioGroupItem.setHintStyle("hint");
		radioGroupItem.setRequiredMessage(I18N.message("fieldrequired"));
		RadioGroupItem.setDefaultProperties(radioGroupItem);

		CheckboxItem checkboxItem = new CheckboxItem();
		checkboxItem.setHintStyle("hint");
		checkboxItem.setRequiredMessage(I18N.message("fieldrequired"));
		CheckboxItem.setDefaultProperties(checkboxItem);

		MultiComboBoxItem multiComboBoxItem = new MultiComboBoxItem();
		multiComboBoxItem.setHintStyle("hint");
		multiComboBoxItem.setRequiredMessage(I18N.message("fieldrequired"));
		MultiComboBoxItem.setDefaultProperties(multiComboBoxItem);

		SpinnerItem spinnerItem = new SpinnerItem();
		spinnerItem.setHintStyle("hint");
		spinnerItem.setWidth(60);
		spinnerItem.setRequiredMessage(I18N.message("fieldrequired"));
		SpinnerItem.setDefaultProperties(spinnerItem);

		PasswordItem passwordItem = new PasswordItem();
		passwordItem.setHintStyle("hint");
		passwordItem.setRequiredMessage(I18N.message("fieldrequired"));
		SpinnerItem.setDefaultProperties(passwordItem);

		StaticTextItem staticTextItem = new StaticTextItem();
		staticTextItem.setWidth(150);
		staticTextItem.setHintStyle("hint");
		staticTextItem.setRequiredMessage(I18N.message("fieldrequired"));
		StaticTextItem.setDefaultProperties(staticTextItem);

		IntegerItem integerItem = new IntegerItem();
		integerItem.setRequiredMessage(I18N.message("fieldrequired"));
		integerItem.setHintStyle("hint");
		IntegerItem.setDefaultProperties(integerItem);

		ColorPickerItem colorPickerItem = new ColorPickerItem();
		colorPickerItem.setWidth(150);
		colorPickerItem.setRequiredMessage(I18N.message("fieldrequired"));
		colorPickerItem.setHintStyle("hint");
		ColorPickerItem.setDefaultProperties(colorPickerItem);

		LinkItem linkItem = new LinkItem();
		linkItem.setRequiredMessage(I18N.message("fieldrequired"));
		linkItem.setHintStyle("hint");
		LinkItem.setDefaultProperties(linkItem);

		TextAreaItem textAreaItem = new TextAreaItem();
		textAreaItem.setRequiredMessage(I18N.message("fieldrequired"));
		textAreaItem.setHintStyle("hint");
		TextAreaItem.setDefaultProperties(textAreaItem);

		TimeItem timeItem = new TimeItem();
		timeItem.setHintStyle("hint");
		timeItem.setWidth(60);
		TimeItem.setDefaultProperties(timeItem);

		FloatItem floatItem = new FloatItem();
		floatItem.setHintStyle("hint");
		FloatItem.setDefaultProperties(floatItem);

		ColorPickerItem colorItemPicker = new ColorPickerItem();
		colorItemPicker.setHintStyle("hint");
		colorItemPicker.setWidth(100);
		ColorPickerItem.setDefaultProperties(colorItemPicker);
	}

	public static ColorPickerItem newColorItemPicker(String name, String title, String value) {
		ColorPickerItem item = new ColorPickerItem(filterItemName(name));
		if (title != null)
			item.setTitle(I18N.message(title));
		else
			item.setShowTitle(false);

		if (value != null)
			item.setValue(value);

		return item;
	}

	/**
	 * Creates a new DateItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (optional)
	 */
	public static DateItem newDateItem(String name, String title) {
		DateItem date = new DateItem(filterItemName(name));
		if (title != null)
			date.setTitle(I18N.message(title));
		else
			date.setShowTitle(false);
		return date;
	}

	/**
	 * Creates a new DateItem for the Extended AttributesDS.
	 * 
	 * @param name The item name (mandatory)
	 */
	public static DateItem newDateItemForAttribute(String name, String title) {
		// We cannot use spaces in items name
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		final DateItem date = new DateItem(itemName);
		date.setTitle(title);
		return date;
	}

	public static MiniDateRangeItem newMiniDateRangeItem(String name, String title) {
		final MiniDateRangeItem date = new MiniDateRangeItem(filterItemName(name));
		if (title != null)
			date.setTitle(I18N.message(title));
		else
			date.setShowTitle(false);
		return date;
	}

	public static MultiComboBoxItem newMultipleUsersSelector(String name, String title, long[] selection) {
		String[] ids = null;

		if (selection != null) {
			ids = new String[selection.length];
			for (int i = 0; i < ids.length; i++)
				ids[i] = Long.toString(selection[i]);
		}

		MultiComboBoxItem item = ItemFactory.newMultiComboBoxItem(name, title, new UsersDS(null, false), ids);
		item.setValueField("id");
		item.setDisplayField("username");

		ComboBoxItem combo = new ComboBoxItem();
		combo.setHint(I18N.message("enterusers"));

		item.setAutoChildProperties("comboBox", combo);
		return item;
	}

	public static SelectItem newUserSelectorForAttribute(String name, String title, String groupIdOrName) {
		final SelectItem item = new UserSelector("_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER), title,
				groupIdOrName, false);
		return item;
	}

	public static SelectItem newRecipientTypeSelector(String name) {
		SelectItem selector = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("to", I18N.message("to"));
		opts.put("cc", I18N.message("cc"));
		selector.setValueMap(opts);
		selector.setName(filterItemName(name));
		selector.setShowTitle(false);
		selector.setValue("to");
		selector.setRequired(true);
		return selector;
	}

	public static SelectItem newDensitySelector() {
		SelectItem densitySelector = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("dense", I18N.message("dense"));
		opts.put("compact", I18N.message("compact"));
		opts.put("medium", I18N.message("mmedium"));
		opts.put("expanded", I18N.message("expanded"));
		opts.put("spacious", I18N.message("spacious"));

		densitySelector.setValueMap(opts);
		densitySelector.setName("density");
		densitySelector.setTitle(I18N.message("density"));
		densitySelector.setWidth(100);

		densitySelector.setValue(Session.get().getConfig("gui.density"));

		return densitySelector;
	}

	public static SelectItem newSkinSelector() {
		SelectItem skinSelector = new SelectItem();
		skinSelector.setWidth(120);
		skinSelector.setName("skin");
		skinSelector.setTitle(I18N.message("skin"));
		skinSelector.setValueField("name");
		skinSelector.setDisplayField("label");
		skinSelector.setOptionDataSource(new SkinsDS());

		if (Session.get() != null && Session.get().getInfo().getBranding() != null)
			skinSelector.setValue(Session.get().getInfo().getBranding().getSkin());

		return skinSelector;
	}

	public static SelectItem newDateOperator(String name, String title) {
		SelectItem dateOperator = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("before", I18N.message("before"));
		opts.put("after", I18N.message("after"));
		dateOperator.setValueMap(opts);
		dateOperator.setName(filterItemName(name));
		if (title != null)
			dateOperator.setTitle(I18N.message(title));
		else
			dateOperator.setShowTitle(false);
		dateOperator.setWidth(100);
		return dateOperator;
	}

	public static SelectItem newSizeOperator(String name, String title) {
		SelectItem item = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("lessthan", I18N.message("lessthan"));
		opts.put("greaterthan", I18N.message("greaterthan"));
		item.setValueMap(opts);
		item.setName(filterItemName(name));
		if (title != null)
			item.setTitle(I18N.message(title));
		else
			item.setShowTitle(false);
		item.setWidth(100);
		return item;
	}

	public static SelectItem newBarcodeTypeSelector(String name, String title, String value) {
		SelectItem item = new SelectItem(name, I18N.message(title));
		item.setWrapTitle(false);
		item.setWidth(110);

		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("CODE_128", "CODE_128");
		opts.put("CODE_39", "CODE_39");
		opts.put("EAN_13", "EAN_13");
		opts.put("EAN_8", "EAN_8");
		opts.put("ITF", "ITF");
		opts.put("UPC_A", "UPC_A");
		opts.put("QR_CODE", "QR_CODE");

		item.setValueMap(opts);

		if (value != null)
			item.setValue(value);

		return item;
	}

	public static SelectItem newLanguageSelector(String name, boolean withEmpty, boolean gui) {
		SelectItem item = new SelectItem();
		if (gui)
			item.setValueMap(I18N.getSupportedGuiLanguages(withEmpty));
		else
			item.setValueMap(I18N.getSupportedLanguages(withEmpty));
		item.setName(filterItemName(name));
		item.setTitle(I18N.message("language"));
		item.setWrapTitle(false);
		item.setWidth(120);
		return item;
	}

	public static SelectItem newCharsetSelector(String name) {
		SelectItem item = new SelectItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message("charset"));
		item.setWrapTitle(false);
		item.setDefaultValue("UTF-8");
		item.setDisplayField("name");
		item.setValueField("code");

		ListGridField code = new ListGridField("code", I18N.message("code"));
		code.setWidth(90);
		code.setHidden(true);
		code.setShowTitle(false);

		ListGridField nameField = new ListGridField("name", I18N.message("name"));
		nameField.setWidth("*");
		nameField.setShowTitle(false);

		item.setPickListFields(code, nameField);
		item.setOptionDataSource(new CharsetsDS());

		return item;
	}

	public static SelectItem newStorageSelector(String name, Integer value) {
		SelectItem item = new SelectItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message("storage"));
		item.setWrapTitle(false);
		item.setDisplayField("name");
		item.setValueField("id");
		item.setWidth(100);

		ListGridField nameField = new ListGridField("name", I18N.message("name"));
		nameField.setWidth("*");
		nameField.setShowTitle(false);

		item.setPickListFields(nameField);
		item.setOptionDataSource(new StoragesDS(true, false));

		if (value != null)
			item.setValue(value.toString());

		return item;
	}

	public static SelectItem newStorageTypeSelector() {
		SelectItem item = new SelectItem("type", I18N.message("type"));
		item.setWidth(100);
		item.setWrapTitle(false);
		item.setDisplayField("name");
		item.setValueField("id");
		item.setOptionDataSource(new StoragesTypesDS());
		return item;
	}

	public static SelectItem newFormatConverterSelector() {
		return newFormatConverterSelector("-", "-");
	}

	public static SelectItem newFormatConverterSelector(String inExt, String outExt) {
		SelectItem item = new SelectItem("converter", I18N.message("converter"));
		item.setWidth(150);
		item.setWrapTitle(false);
		item.setMultiple(false);
		item.setDisplayField("label");
		item.setValueField("converter");
		item.setOptionDataSource(new FormatConvertersDS(inExt, outExt));

		item.setValueFormatter(new FormItemValueFormatter() {

			@Override
			public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
				ListGridRecord r = item.getSelectedRecord();
				String label = r.getAttribute("label");
				if (label == null || "".equals(label))
					return null;

				if (r.getAttribute("eenabled") != null && !r.getAttributeAsBoolean("eenabled"))
					label = "<span style='color:red;'>" + label + "</span>";
				return label;
			}
		});

		ListGridField f = new ListGridField("label");
		f.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				String label = record.getAttribute("label");
				if (label == null || "".equals(label))
					return null;

				if (record.getAttribute("eenabled") != null && !record.getAttributeAsBoolean("eenabled"))
					label = "<span style='color:red;'>" + label + "</span>";
				return label;
			}
		});
		item.setPickListFields(f);

		return item;
	}

	public static TextItem newEmailItem(String name, String title, boolean multiple) {
		TextItem item = new TextItem();
		item.setName(filterItemName(name));
		if (title != null)
			item.setTitle(I18N.message(title));
		else
			item.setShowTitle(false);
		if (multiple)
			item.setValidators(new EmailsValidator());
		else
			item.setValidators(new EmailValidator());
		item.setWrapTitle(false);
		item.setWidth(180);

		return item;
	}

	public static ComboBoxItem newEmailComboSelector(String name, String title) {
		final ComboBoxItem selector = new ComboBoxItem(filterItemName(name));
		selector.setTitle(I18N.message(title));
		selector.setWrapTitle(false);
		selector.setValueField("email");
		selector.setDisplayField("email");
		selector.setPickListWidth(350);
		selector.setFetchDelay(2000);
		selector.setHideEmptyPickList(true);
		ListGridField email = new ListGridField("email", I18N.message("email"));
		email.setWidth("*");
		ListGridField firstName = new ListGridField("firstName", I18N.message("firstname"));
		firstName.setWidth(90);
		ListGridField lastName = new ListGridField("lastName", I18N.message("lastname"));
		lastName.setWidth(90);
		selector.setPickListFields(email, firstName, lastName);
		selector.setOptionDataSource(new ContactsDS());
		return selector;
	}

	public static SelectItem newEmailSelector(String name, String title) {
		final SelectItem selector = new SelectItem(filterItemName(name));
		selector.setTitle(I18N.message(title));
		selector.setWrapTitle(false);
		selector.setValueField("email");
		selector.setDisplayField("email");
		selector.setPickListWidth(350);
		selector.setMultiple(true);
		selector.setHideEmptyPickList(true);
		ListGridField email = new ListGridField("email", I18N.message("email"));
		email.setWidth("*");
		ListGridField firstName = new ListGridField("firstName", I18N.message("firstname"));
		firstName.setWidth(90);
		ListGridField lastName = new ListGridField("lastName", I18N.message("lastname"));
		lastName.setWidth(90);
		selector.setPickListFields(email, firstName, lastName);
		selector.setOptionDataSource(new ContactsDS());
		return selector;
	}

	public static SelectItem newEmailFromSelector(String name, String title) {
		final SelectItem selector = new SelectItem(filterItemName(name));
		if (title != null)
			selector.setTitle(I18N.message(title));
		else
			selector.setTitle(I18N.message("from"));
		selector.setWidth(300);
		selector.setWrapTitle(false);
		selector.setValueField("email");
		selector.setDisplayField("email");
		selector.setHideEmptyPickList(true);

		if (Session.get().getUser().getEmail2() != null && !Session.get().getUser().getEmail2().isEmpty())
			selector.setValueMap(Session.get().getUser().getEmail(), Session.get().getUser().getEmail2());
		else
			selector.setValueMap(Session.get().getUser().getEmail());

		selector.setValue(Session.get().getUser().getEmail());

		return selector;
	}

	public static SelectItem newGroupSelector(String name, String title) {
		SelectItem group = new SelectItem(filterItemName(name));
		group.setTitle(I18N.message(title));
		group.setWrapTitle(false);
		group.setValueField("id");
		group.setDisplayField("name");
		group.setPickListWidth(300);
		ListGridField n = new ListGridField("name", I18N.message("name"));
		ListGridField description = new ListGridField("description", I18N.message("description"));
		group.setPickListFields(n, description);
		group.setOptionDataSource(new GroupsDS());
		return group;
	}

	public static SelectItem newUserSelector(String name, String title, String groupIdOrName, boolean required) {
		SelectItem user = new SelectItem(filterItemName(name));
		user.setTitle(I18N.message(title));
		user.setWrapTitle(false);
		ListGridField id = new ListGridField("id", I18N.message("id"));
		id.setHidden(true);
		ListGridField username = new ListGridField("username", I18N.message("username"));
		ListGridField label = new ListGridField("label", I18N.message("name"));
		user.setValueField("id");
		user.setDisplayField("username");
		user.setPickListWidth(300);
		user.setPickListFields(id, username, label);
		user.setOptionDataSource(new UsersDS(groupIdOrName, required));
		return user;
	}

	public static SelectItem newTenantSelector() {
		SelectItem tenant = new SelectItem("tenant");
		tenant.setTitle(I18N.message("tenant"));
		tenant.setWrapTitle(false);
		ListGridField id = new ListGridField("id", I18N.message("id"));
		id.setHidden(true);
		ListGridField _name = new ListGridField("name", I18N.message("name"));
		ListGridField displayName = new ListGridField("displayName", I18N.message("displayname"));
		tenant.setValueField("id");
		tenant.setDisplayField("displayName");
		tenant.setPickListWidth(300);
		tenant.setWidth(150);
		tenant.setPickListFields(id, _name, displayName);
		tenant.setOptionDataSource(new TenantsDS());
		return tenant;
	}

	public static RadioGroupItem newRadioGroup(String name, String title) {
		RadioGroupItem radioGroupItem = new RadioGroupItem();
		radioGroupItem.setName(filterItemName(name));
		radioGroupItem.setVertical(false);
		radioGroupItem.setTitle(I18N.message(title));
		radioGroupItem.setWidth(80);
		return radioGroupItem;
	}

	public static RadioGroupItem newBooleanSelector(String name, String title) {
		RadioGroupItem radioGroupItem = newRadioGroup(name, title);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("yes", I18N.message("yes"));
		map.put("no", I18N.message("no"));
		radioGroupItem.setValueMap(map);
		return radioGroupItem;
	}

	public static CheckboxItem newCheckbox(String name, String title) {
		CheckboxItem item = new CheckboxItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		return item;
	}

	public static MultiComboBoxItem newMultiComboBoxItem(String name, String title, DataSource options, Object[] values) {
		MultiComboBoxItem item = new MultiComboBoxItem(name, I18N.message(title));
		item.setLayoutStyle(MultiComboBoxLayoutStyle.FLOW);
		item.setWidth(200);
		item.setMultiple(true);
		if (options != null) {
			item.setOptionDataSource(options);
			item.setAutoFetchData(true);
		}

		IButton closeButton = new IButton();
		closeButton.setIcon("[SKIN]/headerIcons/close.gif");
		closeButton.setIconOrientation("body");
		item.setButtonProperties(closeButton);

		if (values != null) {
			item.setValue(values);
		}

		return item;
	}

	public static MultiComboBoxItem newTagsComboBoxItem(String name, String title, TagsDS options, Object[] tags) {
		MultiComboBoxItem item = newMultiComboBoxItem(name, title, options, tags);
		item.setPrompt(I18N.message("typeatag"));
		item.setValueField("word");
		item.setDisplayField("word");
		return item;
	}

	public static SelectItem newTagsMultiplePickList(String name, String title, TagsDS options, Object[] tags) {
		final SelectItem item = newSelectItem(name, title);
		item.setMultiple(true);
		item.setMultipleAppearance(MultipleAppearance.PICKLIST);
		item.setValueField("word");
		item.setDisplayField("word");
		item.setOptionDataSource(options);
		if (tags != null)
			item.setValue(tags);
		return item;
	}

	public static SelectItem newMultipleSelector(String name, String title) {
		SelectItem selectItemMultipleGrid = new SelectItem();
		selectItemMultipleGrid.setName(filterItemName(name));
		selectItemMultipleGrid.setTitle(I18N.message(title));
		selectItemMultipleGrid.setMultiple(true);
		selectItemMultipleGrid.setValueMap("");
		return selectItemMultipleGrid;
	}

	public static SelectItem newPrioritySelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), title);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", I18N.message("low"));
		map.put("1", I18N.message("medium"));
		map.put("2", I18N.message("high"));
		select.setValueMap(map);
		select.setValue("0");
		return select;
	}

	public static SelectItem newWelcomeScreenSelector(String name, Integer value) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message("welcomescreen"));
		select.setWidth(150);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("1500", I18N.message("documents"));
		map.put("1510", I18N.message("search"));
		map.put("1520", I18N.message("dashboard"));
		select.setValueMap(map);
		if (value != null)
			select.setValue(value.toString());
		else
			select.setValue("1500");
		return select;
	}

	public static SelectItem newDashletSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), title);
		select.setAllowEmptyValue(false);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("" + Constants.DASHLET_CHECKOUT, I18N.message(Constants.EVENT_CHECKEDOUT + "docs"));
		map.put("" + Constants.DASHLET_CHECKIN, I18N.message(Constants.EVENT_CHECKEDIN + "docs"));
		map.put("" + Constants.DASHLET_LOCKED, I18N.message(Constants.EVENT_LOCKED + "docs"));
		map.put("" + Constants.DASHLET_CHANGED, I18N.message(Constants.EVENT_CHANGED + "docs"));
		map.put("" + Constants.DASHLET_DOWNLOADED, I18N.message(Constants.EVENT_DOWNLOADED + "docs"));
		map.put("" + Constants.DASHLET_LAST_NOTES, I18N.message("lastnotes"));
		map.put("" + Constants.DASHLET_TAGCLOUD, I18N.message("tagcloud"));

		select.setValueMap(map);
		select.setValue(map.keySet().iterator().next());
		return select;
	}

	public static SelectItem newEventsSelector(String name, String title, boolean folder, boolean workflow, boolean user) {
		SelectItem select = newMultipleSelector(filterItemName(name), title);
		select.setWidth(350);
		select.setHeight(250);
		select.setMultipleAppearance(MultipleAppearance.GRID);
		select.setMultiple(true);
		select.setOptionDataSource(new EventsDS(folder, workflow, user));
		select.setValueField("code");
		select.setDisplayField("label");
		return select;
	}

	public static SelectItem newSelectItem(String name, String title) {
		SelectItem select = newMultipleSelector(filterItemName(name),
				title != null ? I18N.message(title) : I18N.message(name));
		select.setMultiple(false);
		select.setWrapTitle(false);
		return select;
	}

	public static SelectItem newConversionFormatItem(String fileName) {
		SelectItem select = ItemFactory.newSelectItem("format", I18N.message("format"));
		select.setMultiple(false);
		select.setWrapTitle(false);
		select.setOptionDataSource(new ConversionFormatsDS(fileName));
		select.setValueField("extension");
		select.setDisplayField("extension");
		return select;
	}

	public static SelectItem newYesNoSelectItem(String name, String title) {
		SelectItem item = newSelectItem(name, title);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("true", I18N.message("yes"));
		map.put("false", I18N.message("no"));
		item.setValueMap(map);
		item.setValue("true");
		item.setWidth(50);
		return item;
	}

	public static SpinnerItem newSpinnerItem(String name, String title, Long value) {
		SpinnerItem spinner = new SpinnerItem(name);
		spinner.setTitle(I18N.message(title));
		spinner.setMin(0);
		spinner.setStep(1);
		spinner.setWidth(60);
		if (value != null)
			spinner.setValue(value.intValue());
		else
			spinner.setValue((Integer) null);
		return spinner;
	}

	public static SpinnerItem newSpinnerItem(String name, String title, Integer value) {
		if (value != null)
			return newSpinnerItem(name, title, new Long(value));
		else
			return newSpinnerItem(name, title, (Long) null);
	}

	public static Img newImgIcon(String name) {
		Img img = newImg(name);
		img.setWidth("16px");
		return img;
	}

	public static Img newImg(String name) {
		Img img = new Img("[SKIN]/" + name);
		return img;
	}

	public static Img newBrandImg(String name, GUIInfo info) {
		Img img = null;

		if (name.equals("logo.png"))
			img = new Img(info.getBranding().getLogoSrc());
		else if (name.equals("logo_head.png"))
			img = new Img(info.getBranding().getLogoHeadSrc());
		else if (name.equals("logo_oem.png"))
			img = new Img(info.getBranding().getLogoOemSrc());
		else if (name.equals("logo_head_oem.png"))
			img = new Img(info.getBranding().getLogoHeadOemSrc());
		else if (name.equals("banner.png"))
			img = new Img(info.getBranding().getBannerSrc());
		else if (name.equals("favicon.png"))
			img = new Img(info.getBranding().getFaviconSrc());
		return img;
	}

	public static FormItemIcon newItemIcon(String image) {
		FormItemIcon icon = new FormItemIcon();
		icon.setSrc(ItemFactory.newImgIcon(image).getSrc());
		return icon;
	}

	public static HeaderIcon newHeaderIcon(String image) {
		HeaderIcon icon = new HeaderIcon(ItemFactory.newImgIcon(image).getSrc());
		return icon;
	}

	/**
	 * Creates a new TextItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static TextItem newTextItem(String name, String title, String value) {
		TextItem item = new TextItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		item.setWrapTitle(false);
		item.setRequiredMessage(I18N.message("fieldrequired"));
		return item;
	}

	public static RowSpacerItem newRowSpacer() {
		RowSpacerItem item = new RowSpacerItem();
		item.setCellStyle("row");
		item.setHeight(5);
		return item;
	}

	/**
	 * Creates a new TextItem for the Extended AttributesDS.
	 */
	public static FormItem newStringItemForAttribute(GUIAttribute att) {
		// We cannot use spaces in items name
		String itemName = "_" + att.getName().replaceAll(" ", Constants.BLANK_PLACEHOLDER);

		FormItem item = null;
		if (att.getEditor() == GUIAttribute.EDITOR_TEXTAREA) {
			item = newTextAreaItem(itemName, itemName, att.getStringValue());
			item.setWidth(Session.get().getConfigAsInt("gui.textarea.w"));
			item.setHeight(Session.get().getConfigAsInt("gui.textarea.h"));
		} else {
			item = newTextItem(itemName, itemName, att.getStringValue());
			if (!InputValues.getInputs(itemName).isEmpty()) {
				item = new ComboBoxItem(itemName);
				item.setValueMap(InputValues.getInputsAsStrings(itemName).toArray(new String[0]));
				item.setShowPickerIcon(false);
				item.setTextBoxStyle("textItem");
			}

			if (att.getEditor() == GUIAttribute.EDITOR_LISTBOX && att.getSetId() != null) {
				item = new SelectItem();
				item.setOptionDataSource(new AttributeOptionsDS(att.getSetId(), att.getName(), !att.isMandatory()));

				ListGridField value = new ListGridField("value", I18N.message("value"));
				((SelectItem) item).setPickListWidth(200);
				((SelectItem) item).setPickListFields(value);
				((SelectItem) item).setValueField("value");
				((SelectItem) item).setDisplayField("value");
			}

			item.setWidth(Session.get().getConfigAsInt("gui.textbox.w"));
		}

		item.setName(itemName);
		if (att.getLabel() != null)
			item.setTitle(att.getLabel());
		else
			item.setTitle(att.getName());
		item.setWrapTitle(false);

		return item;
	}

	public static PasswordItem newPasswordItem(String name, String title, String value) {
		PasswordItem password = new PasswordItem();
		password.setTitle(I18N.message(title));
		password.setName(filterItemName(name));
		if (value != null)
			password.setValue(value);
		return password;
	}

	/**
	 * Creates a new TextItem that validates a simple text.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static TextItem newSimpleTextItem(String name, String title, String value) {
		TextItem item = newTextItem(filterItemName(name), I18N.message(title), value);
		item.setValidators(new SimpleTextValidator());
		return item;
	}

	/**
	 * Creates a new StaticTextItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static StaticTextItem newStaticTextItem(String name, String title, String value) {
		StaticTextItem item = new StaticTextItem();
		if (name.trim().isEmpty())
			item.setShouldSaveValue(false);
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		item.setWrapTitle(false);
		return item;
	}

	/**
	 * Creates a new IntegerItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static IntegerItem newLongItem(String name, String title, Long value) {
		IntegerItem item = new IntegerItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		IsIntegerValidator iv = new IsIntegerValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		item.setValidators(iv);
		return item;
	}

	/**
	 * Creates a new IntegerItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static IntegerItem newIntegerItem(String name, String title, Integer value) {
		IntegerItem item = new IntegerItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		IsIntegerValidator iv = new IsIntegerValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		item.setValidators(iv);
		return item;
	}

	/**
	 * Creates a new IntegerItem for the Extended AttributesDS.
	 * 
	 * @param name The item name (mandatory)
	 * @param value The item value (optional)
	 */
	public static FormItem newIntegerItemForAttribute(String name, String label, Integer value) {
		// We cannot use spaces in items name
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		FormItem item = newIntegerItem(itemName, label, value);
		if (!InputValues.getInputs(item.getName()).isEmpty()) {
			item = formItemWithSuggestions(item);
			IsIntegerValidator iv = new IsIntegerValidator();
			iv.setErrorMessage(I18N.message("wholenumber"));
			item.setValidators(iv);
		}
		return item;
	}

	/**
	 * Simple boolean selector for Extended Attribute
	 */
	public static SelectItem newBooleanSelectorForAttribute(String name, String title, boolean allowEmpty) {
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		SelectItem select = new SelectItem();
		select.setName(itemName);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (allowEmpty)
			map.put("", " ");

		map.put("1", I18N.message("yes"));
		map.put("0", I18N.message("no"));
		select.setValueMap(map);
		select.setTitle(I18N.message(title));
		select.setWidth(80);
		select.setValue("");

		return select;
	}

	/**
	 * Creates a new IntegerItem with a range validator.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 * @param min The item minimum value (optional)
	 * @param min The item maximum value (optional)
	 */
	public static IntegerItem newValidateIntegerItem(String name, String title, Integer value, Integer min, Integer max) {
		IntegerItem item = newIntegerItem(filterItemName(name), I18N.message(title), value);
		IntegerRangeValidator rv = null;
		if (min != null || max != null) {
			rv = new IntegerRangeValidator();
			if (min != null)
				rv.setMin(min);
			if (max != null)
				rv.setMax(max);
		}
		IsIntegerValidator iv = new IsIntegerValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		if (rv == null)
			item.setValidators(iv);
		else
			item.setValidators(iv, rv);
		return item;
	}

	/**
	 * Creates a new SpinnerItem( with a range validator.
	 * 
	 */
	public static SpinnerItem newSpinnerItem(String name, String title, Integer value, Integer min, Integer max) {
		SpinnerItem spinner = new SpinnerItem(name);
		spinner.setTitle(I18N.message(title));
		spinner.setWrapTitle(false);
		spinner.setDefaultValue(value);
		if (min != null)
			spinner.setMin(min);
		if (max != null)
			spinner.setMax(max);
		spinner.setStep(1);
		spinner.setWriteStackedIcons(true);
		return spinner;
	}

	public static LinkItem newLinkItem(String name, String title) {
		LinkItem linkItem = new LinkItem(filterItemName(name));
		if (!title.trim().isEmpty()) {
			linkItem.setTitle(I18N.message(title));
			linkItem.setLinkTitle(I18N.message(title));
		}
		linkItem.setWrapTitle(false);
		return linkItem;
	}

	/**
	 * Creates a new TextAreaItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static TextAreaItem newTextAreaItem(String name, String title, String value) {
		TextAreaItem item = new TextAreaItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		item.setHeight(50);
		item.setWidth("100%");
		if (value != null)
			item.setValue(value);
		else
			item.setValue("");
		return item;
	}

	public static SelectItem newDueTimeSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));
		select.setWidth(90);
		select.setShowTitle(false);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("minute", I18N.message("minutes"));
		map.put("hour", I18N.message("hours"));
		map.put("day", I18N.message("ddays"));
		select.setValueMap(map);
		select.setValue("minute");
		return select;
	}

	public static TimeItem newTimeItem(String name, String title) {
		TimeItem item = new TimeItem(name, I18N.message(title));
		return item;
	}

	public static SelectItem newTemplateSelector(boolean withEmpty, Long templateId) {
		SelectItem templateItem = new SelectItem("template", I18N.message("template"));
		templateItem.setDisplayField("name");
		templateItem.setValueField("id");
		templateItem.setWidth(150);
		templateItem.setMultiple(false);
		templateItem.setWrapTitle(false);
		templateItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
		templateItem.setOptionDataSource(new TemplatesDS(withEmpty, templateId, null));

		if (!Feature.enabled(Feature.TEMPLATE))
			templateItem.setDisabled(true);
		if (templateId != null)
			templateItem.setValue(templateId.toString());
		return templateItem;
	}

	public static SelectItem newAttributeSetSelector() {
		final SelectItem selectItem = new SelectItem("attributeset", I18N.message("attributeset"));
		selectItem.setMultiple(false);
		selectItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItem.setDisplayField("name");
		selectItem.setValueField("id");
		selectItem.setWidth(120);
		selectItem.setOptionDataSource(new AttributeSetsDS(false, GUIAttributeSet.TYPE_DEFAULT));
		selectItem.setWrapTitle(false);

		return selectItem;
	}

	public static SelectItem newAttributesSelector() {
		final SelectItem selectItem = new SelectItem("attributes", I18N.message("attributes"));
		selectItem.setMultiple(true);
		selectItem.setMultipleAppearance(MultipleAppearance.PICKLIST);
		selectItem.setDisplayField("label");
		selectItem.setValueField("name");
		selectItem.setPickListWidth(150);
		selectItem.setOptionDataSource(new AttributesDS());
		selectItem.setWrapTitle(false);
		return selectItem;
	}

	public static SelectItem newFrequencySelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", "");
		map.put("1", I18N.message("daily"));
		map.put("7", I18N.message("weekly"));
		map.put("15", I18N.message("biweekly"));
		map.put("30", I18N.message("monthly"));
		map.put("180", I18N.message("sixmonthly"));
		map.put("365", I18N.message("yearly"));

		select.setValueMap(map);
		select.setWidth(100);
		return select;
	}

	public static SelectItem newEventStatusSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", "");
		map.put("1", I18N.message("working"));
		map.put("2", I18N.message("completed"));
		map.put("3", I18N.message("canceled"));

		select.setValueMap(map);
		select.setWidth(90);
		return select;
	}

	public static SelectItem newEmailProtocolSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));
		select.setWidth(110);
		select.setValueMap("pop3", "imap");
		return select;
	}

	public static SelectItem newEmailFolderingSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));
		select.setWidth(110);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", I18N.message("none"));
		map.put("1", I18N.message("year"));
		map.put("2", I18N.message("month"));
		map.put("3", I18N.message("day"));
		select.setValueMap(map);
		return select;
	}

	public static SelectItem newEffectSelector(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));
		select.setWidth(110);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "");
		map.put("move", I18N.message("move"));
		map.put("copy", I18N.message("copy"));
		select.setValueMap(map);
		return select;
	}

	public static SelectItem newEmailFields(String name, String title) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message(title));
		select.setWidth(110);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", I18N.message("subject"));
		map.put("1", I18N.message("sender"));
		map.put("2", I18N.message("content"));
		map.put("3", I18N.message("recipient"));
		select.setValueMap(map);
		return select;
	}

	public static SelectItem newAliasTypeSelector() {
		SelectItem item = new SelectItem();
		item.setName("aliastype");
		item.setTitle(I18N.message("type"));
		item.setWrapTitle(false);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", I18N.message("document"));
		map.put("conversion.pdf", I18N.message("pdfconversion"));

		item.setValueMap(map);
		return item;
	}

	public static SelectItem newArchiveSelector(int mode, Integer status) {
		SelectItem item = new SelectItem("archive");
		item.setTitle("");
		item.setRequiredMessage(I18N.message("fieldrequired"));
		ListGridField name = new ListGridField("name", I18N.message("name"));
		ListGridField description = new ListGridField("description", I18N.message("description"));
		item.setValueField("id");
		item.setDisplayField("name");
		item.setPickListWidth(300);
		item.setPickListFields(name, description);
		item.setOptionDataSource(new ArchivesDS(mode, null, status, null));
		if (!Feature.enabled(Feature.IMPEX))
			item.setDisabled(true);
		return item;
	}

	public static SelectItem newImportCustomIds() {
		SelectItem item = newSelectItem("importcids", null);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put(Integer.toString(GUIArchive.CUSTOMID_NOT_IMPORT), I18N.message("ignore"));
		map.put(Integer.toString(GUIArchive.CUSTOMID_IMPORT_AND_NEW_RELEASE), I18N.message("importasnewversion"));
		map.put(Integer.toString(GUIArchive.CUSTOMID_IMPORT_AND_NEW_SUBVERSION), I18N.message("importasnewsubversion"));
		map.put(Integer.toString(GUIArchive.CUSTOMID_IMPORT_AND_NEW_DOCUMENT), I18N.message("importasnewdoc"));
		item.setValueMap(map);
		return item;
	}

	public static SelectItem newWorkflowSelector() {
		SelectItem item = new SelectItem("workflow", I18N.message("workflow"));
		item.setRequiredMessage(I18N.message("fieldrequired"));
		ListGridField name = new ListGridField("name", I18N.message("name"));
		ListGridField description = new ListGridField("description", I18N.message("description"));
		item.setWidth(250);
		item.setPickListWidth(300);
		item.setPickListFields(name, description);
		item.setDisplayField("name");
		item.setValueField("id");
		item.setWrapTitle(false);
		item.setOptionDataSource(new WorkflowsDS(false, true));
		if (!Feature.enabled(Feature.WORKFLOW))
			item.setDisabled(true);
		;
		return item;
	}

	public static SelectItem newFormSelector() {
		SelectItem item = new SelectItem("form", I18N.message("form"));
		item.setRequiredMessage(I18N.message("fieldrequired"));
		ListGridField name = new ListGridField("name", I18N.message("name"));
		item.setPickListFields(name);
		item.setDisplayField("name");
		item.setValueField("id");
		item.setWrapTitle(false);
		item.setOptionDataSource(new FormsDS());
		if (!Feature.enabled(Feature.FORM))
			item.setDisabled(true);
		return item;
	}

	public static SelectItem newStampSelector() {
		SelectItem item = new SelectItem("stamp", I18N.message("stamp"));
		item.setRequiredMessage(I18N.message("fieldrequired"));
		ListGridField name = new ListGridField("name", I18N.message("name"));
		item.setWidth(200);
		item.setPickListWidth(200);
		item.setPickListFields(name);
		item.setDisplayField("name");
		item.setValueField("id");
		item.setWrapTitle(false);
		item.setOptionDataSource(new StampsDS(Session.get().getUser().getId(), true));
		if (!Feature.enabled(Feature.STAMP))
			item.setDisabled(true);
		return item;
	}

	public static Label newLinkLabel(String title) {
		Label label = new Label(I18N.message(title));
		label.setWrap(false);
		label.setCursor(Cursor.HAND);
		label.setAutoWidth();
		return label;
	}

	/**
	 * Creates a new FloatItem.
	 * 
	 * @param name The item name (mandatory)
	 * @param title The item title (mandatory)
	 * @param value The item value (optional)
	 */
	public static FloatItem newFloatItem(String name, String title, Float value) {
		FloatItem item = new FloatItem();
		item.setName(filterItemName(name));
		item.setTitle(I18N.message(title));
		if (value != null)
			item.setValue(value);
		IsFloatValidator iv = new IsFloatValidator();
		iv.setErrorMessage(I18N.message("wholenumber"));
		item.setValidators(iv);
		return item;
	}

	/**
	 * Creates a new FloatItem for the Extended AttributesDS.
	 * 
	 * @param name The item name (mandatory)
	 * @param value The item value (optional)
	 */
	public static FormItem newFloatItemForAttribute(String name, String label, Float value) {
		// We cannot use spaces in items name
		String itemName = "_" + name.replaceAll(" ", Constants.BLANK_PLACEHOLDER);
		FormItem item = newFloatItem(itemName, label, value);
		if (!InputValues.getInputs(item.getName()).isEmpty()) {
			item = formItemWithSuggestions(item);
			IsFloatValidator iv = new IsFloatValidator();
			iv.setErrorMessage(I18N.message("wholenumber"));
			item.setValidators(iv);
		}
		return item;
	}

	private static FormItem formItemWithSuggestions(FormItem srcItem) {
		FormItem item = null;
		item = new ComboBoxItem(srcItem.getName(), srcItem.getTitle());
		item.setValueMap(InputValues.getInputsAsStrings(srcItem.getName()).toArray(new String[0]));
		item.setShowPickerIcon(false);
		item.setTextBoxStyle("textItem");
		if (srcItem.getValue() != null)
			item.setValue(srcItem.getValue());
		return item;
	}

	/**
	 * Simple yes/no radio button. yes=true, no=false
	 */
	public static RadioGroupItem newYesNoRadioItem(String name, String label) {
		RadioGroupItem item = new RadioGroupItem(filterItemName(name), I18N.message(label));
		item.setVertical(false);
		item.setShowTitle(true);
		item.setWrap(false);
		item.setWrapTitle(false);

		LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
		values.put("true", I18N.message("yes"));
		values.put("false", I18N.message("no"));
		item.setValueMap(values);
		item.setValue("true");

		return item;
	}

	public static SelectItem newTagInputMode(String name, String title) {
		SelectItem mode = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("free", I18N.message("free"));
		opts.put("preset", I18N.message("preset"));
		mode.setValueMap(opts);
		mode.setName(filterItemName(name));
		if (title != null)
			mode.setTitle(I18N.message(title));
		else
			mode.setShowTitle(false);
		mode.setWrapTitle(false);
		mode.setDefaultValue("free");
		mode.setWidth(150);
		return mode;
	}

	public static SelectItem newRunlevelSelector() {
		SelectItem item = new SelectItem("runlevel", I18N.message("currentrunlevel"));
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("default", I18N.message("default"));
		opts.put("bulkload", I18N.message("bulkload"));
		opts.put("devel", I18N.message("devel"));
		item.setValueMap(opts);
		item.setWrapTitle(false);
		item.setValue(Session.get().getConfig("runlevel"));
		item.setRequired(true);
		item.setWidth(150);
		item.setDisabled("demo".equals(Session.get().getConfig("runlevel")));
		return item;
	}

	public static SelectItem newFolderTemplateSelector() {
		SelectItem item = new SelectItem("foldertemplate");
		item.setTitle(I18N.message("foldertemplate"));
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setValueField("id");
		item.setDisplayField("name");
		item.setWrapTitle(false);
		item.setOptionDataSource(new FolderTemplatesDS());
		if (!Feature.enabled(Feature.FOLDER_TEMPLATE))
			item.setDisabled(true);
		return item;
	}

	public static SelectItem newWorkspaceSelector(Long value) {
		SelectItem item = new SelectItem("workspace");
		item.setTitle(I18N.message("workspace"));
		item.setRequiredMessage(I18N.message("fieldrequired"));
		item.setValueField("folderId");
		item.setDisplayField("name");
		item.setWrapTitle(false);
		item.setOptionDataSource(new FoldersDS("profile-workspace"));
		item.setValue(value);
		item.setVisible(Feature.enabled(Feature.MULTI_WORKSPACE));
		item.setWidth(150);
		return item;
	}

	public static SelectItem newRetentionDateOption(int value) {
		SelectItem selector = new SelectItem("dateoption", I18N.message("dateoption"));
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("" + GUIRetentionPolicy.DATE_OPT_CREATION, I18N.message("created"));
		opts.put("" + GUIRetentionPolicy.DATE_OPT_PUBLISHED, I18N.message("published"));
		opts.put("" + GUIRetentionPolicy.DATE_OPT_STOPPUBLISHING, I18N.message("stoppublishing"));
		opts.put("" + GUIRetentionPolicy.DATE_OPT_ARCHIVED, I18N.message("archiveds"));
		selector.setValueMap(opts);

		selector.setWrapTitle(false);
		selector.setWidth(150);

		selector.setValue("" + value);
		selector.setDefaultValue("" + value);

		return selector;
	}

	public static SelectItem newImportFolderProviderOption(String value) {
		SelectItem selector = new SelectItem("provider", I18N.message("type"));
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		if (Feature.enabled(Feature.IMPORT_LOCAL_FOLDERS))
			opts.put(GUIImportFolder.PROVIDER_FILE, I18N.message("localfolder"));
		if (Feature.enabled(Feature.IMPORT_REMOTE_FOLDERS)) {
			opts.put(GUIImportFolder.PROVIDER_SMB, I18N.message("smbshare"));
			opts.put(GUIImportFolder.PROVIDER_FTP, I18N.message("fftp"));
			opts.put(GUIImportFolder.PROVIDER_FTPS, I18N.message("ftps"));
			opts.put(GUIImportFolder.PROVIDER_SFTP, I18N.message("sftp"));
		}
		selector.setValueMap(opts);

		selector.setWrapTitle(false);
		selector.setWidth(150);

		selector.setValue(value);
		selector.setDefaultValue(value);

		return selector;
	}

	public static SelectItem newRetentionAction(int value) {
		SelectItem selector = new SelectItem("action", I18N.message("action"));
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("" + GUIRetentionPolicy.ACTION_ARCHIVE, I18N.message("archive"));
		opts.put("" + GUIRetentionPolicy.ACTION_UNPUBLISH, I18N.message("unpublish"));
		opts.put("" + GUIRetentionPolicy.ACTION_DELETE, I18N.message("ddelete"));
		selector.setValueMap(opts);

		selector.setWrapTitle(false);
		selector.setWidth(150);

		selector.setValue("" + value);
		selector.setDefaultValue("" + value);

		return selector;
	}

	public static SelectItem new2AFMethodSelector(String name, String value) {
		SelectItem select = new SelectItem(filterItemName(name), I18N.message("authenticationmethod"));
		select.setWidth(150);
		select.setWrapTitle(false);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", I18N.message("disable2fa"));
		if (Session.get().getTenantConfigAsBoolean("2fa." + Constants.TWOFA_GOOGLE_AUTHENTICATOR + ".enabled"))
			map.put(Constants.TWOFA_GOOGLE_AUTHENTICATOR, "Google Authenticator");
		if (Session.get().getTenantConfigAsBoolean("2fa." + Constants.TWOFA_YUBIKEY + ".enabled"))
			map.put(Constants.TWOFA_YUBIKEY, "Yubikey");
		select.setValueMap(map);
		if (value != null && map.get(value) != null)
			select.setValue(value.toString());
		else
			select.setValue("");
		return select;
	}

	/**
	 * Filter the name from problematic chars
	 */
	public static String filterItemName(String name) {
		return name.replaceAll("\\.", "_");
	}

	/**
	 * Obtain the original item name
	 */
	public static String originalItemName(String name) {
		return name.replaceAll("_", "\\.");
	}
}