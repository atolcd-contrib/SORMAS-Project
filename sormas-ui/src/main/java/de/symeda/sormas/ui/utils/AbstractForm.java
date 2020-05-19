package de.symeda.sormas.ui.utils;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.*;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UserProvider;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractForm<T> extends CustomField<T> {
	protected final String propertyI18nPrefix;
	private final BeanFieldGroup<T> fieldGroup;
	private Class<T> type;
	private List<Field<?>> customFields = new ArrayList<>();

	protected AbstractForm(Class<T> type, String propertyI18nPrefix, UserRight editOrCreateUserRight) {
		this(type, propertyI18nPrefix, editOrCreateUserRight, true);
	}

	protected AbstractForm(Class<T> type, String propertyI18nPrefix, UserRight editOrCreateUserRight, boolean addFields) {
		this.type = type;
		this.propertyI18nPrefix = propertyI18nPrefix;

		fieldGroup = new BeanFieldGroup<T>(type) {

			@Override
			protected void configureField(Field<?> field) {

				field.setBuffered(isBuffered());
				if (!isEnabled()) {
					field.setEnabled(false);
				}

				if (field.getPropertyDataSource().isReadOnly()) {
					field.setReadOnly(true);
				} else if (isReadOnly()) {
					field.setReadOnly(true);
				}
			}
		};

		fieldGroup.setFieldFactory(new SormasFieldGroupFieldFactory(editOrCreateUserRight));
		setHeightUndefined();

		if (addFields) {
			addFields();
		}

		if (editOrCreateUserRight != null && !UserProvider.getCurrent().hasUserRight(editOrCreateUserRight)) {
			fieldGroup.setReadOnly(true);
		}
	}

	protected abstract String createHtmlLayout();

	protected abstract void addFields();

	@Override
	public Class<? extends T> getType() {
		return type;
	}

	@Override
	protected CustomLayout getContent() {
		return (CustomLayout) super.getContent();
	}

	@Override
	public CustomLayout initContent() {

		String htmlLayout = createHtmlLayout();
		CustomLayout layout = new CustomLayout();
		layout.setTemplateContents(htmlLayout);
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setHeightUndefined();

		return layout;
	}

	public BeanFieldGroup<T> getFieldGroup() {
		return this.fieldGroup;
	}

	public List<Field<?>> getCustomFields() {
		return customFields;
	}

	protected void addFields(String... properties) {
		for (String property : properties) {
			addField(property);
		}
	}

	public void addFields(FieldConfiguration... properties) {
		addFields(getContent(), properties);
	}

	public void addFields(CustomLayout layout, String... properties) {
		for (String property : properties) {
			addField(layout, property);
		}
	}

	public void addFields(CustomLayout layout, FieldConfiguration... properties) {
		for (FieldConfiguration property : properties) {
			addField(layout, property);
		}
	}

	protected <T extends Field> T addField(FieldConfiguration configuration) {
		return addField(getContent(), configuration);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected <T extends Field> T addField(CustomLayout layout, FieldConfiguration configuration) {
		Field field = addField(layout, configuration.getPropertyId());

		applyFieldConfiguration(configuration, field);

		return (T) field;
	}

	protected <T extends Field> T addField(CustomLayout layout, Class<T> fieldType, FieldConfiguration configuration) {
		Field field = addField(layout, configuration.getPropertyId(), fieldType);

		applyFieldConfiguration(configuration, field);

		return (T) field;
	}

	protected void applyFieldConfiguration(FieldConfiguration configuration, Field field) {
		if (configuration.getWidth() != null) {
			field.setWidth(configuration.getWidth(), configuration.getWidthUnit());
		}

		if (configuration.getCaption() != null) {
			field.setCaption(configuration.getCaption());
		}

		if (configuration.getDescription() != null && field instanceof AbstractField) {
			((AbstractField) field).setDescription(configuration.getDescription());
		}

		if (configuration.getStyle() != null) {
			CssStyles.style(field, configuration.getStyle());
		}
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Field> void addFields(Class<T> fieldType, String... properties) {
		for (String property : properties) {
			addField(property, fieldType);
		}
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Field> void addFields(CustomLayout layout, Class<T> fieldType, String... properties) {
		for (String property : properties) {
			addField(layout, property, fieldType);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected <T extends Field> T addField(String propertyId) {
		return (T) addField(propertyId, Field.class);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected <T extends Field> T addField(CustomLayout layout, String propertyId) {
		return (T) addField(layout, propertyId, Field.class);
	}

	protected <T extends Field> T addField(String propertyId, Class<T> fieldType) {
		return addField(getContent(), propertyId, fieldType);
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Field> T addField(CustomLayout layout, String propertyId, Class<T> fieldType) {
		T field = getFieldGroup().buildAndBind(propertyId, (Object) propertyId, fieldType);
		formatField(field, propertyId);
		field.setId(propertyId);
		layout.addComponent(field, propertyId);
		addDefaultAdditionalValidators(field);
		return field;
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Field> T addCustomField(String fieldId, Class<?> dataType, Class<T> fieldType) {
		T field = getFieldGroup().getFieldFactory().createField(dataType, fieldType);
		field.setId(fieldId);
		formatField(field, fieldId);
		addDefaultAdditionalValidators(field);
		getContent().addComponent(field, fieldId);
		customFields.add(field);
		return field;
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Field> void formatField(T field, String propertyId) {
	}

	@SuppressWarnings("rawtypes")
	/**
	 * @param allowedDaysInFuture How many days in the future the value of this field can be or
	 * -1 for no restriction at all
	 */
	protected <T extends Field> T addDateField(String propertyId, Class<T> fieldType, int allowedDaysInFuture) {
		T field = getFieldGroup().buildAndBind(propertyId, (Object) propertyId, fieldType);
		formatField(field, propertyId);
		field.setId(propertyId);
		getContent().addComponent(field, propertyId);
		addFutureDateValidator(field, allowedDaysInFuture);
		return field;
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Field> T addDefaultAdditionalValidators(T field) {
		addFutureDateValidator(field, 0);
		return field;
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Field> T addFutureDateValidator(T field, int amountOfDays) {
		if (amountOfDays < 0) {
			return field;
		}

		if (DateField.class.isAssignableFrom(field.getClass())
				|| DateTimeField.class.isAssignableFrom(field.getClass())) {
			field.addValidator(new FutureDateValidator(field, amountOfDays, field.getCaption()));
		}

		return field;
	}

	public Field<?> getField(String fieldOrPropertyId) {
		Field<?> field = getFieldGroup().getField(fieldOrPropertyId);
		if (field == null) {
			// try to get the field from the layout
			Component component = getContent().getComponent(fieldOrPropertyId);
			if (component instanceof Field<?>) {
				field = (Field<?>) component;
			}
		}
		return field;
	}

	@Override
	protected T getInternalValue() {
		BeanItem<T> beanItem = getFieldGroup().getItemDataSource();
		if (beanItem == null) {
			return null;
		} else {
			return beanItem.getBean();
		}
	}

	@Override
	protected void setInternalValue(T newValue) {
		super.setInternalValue(newValue);
		BeanFieldGroup<T> fieldGroup = getFieldGroup();
		fieldGroup.setItemDataSource(newValue);
	}

	protected String getPropertyI18nPrefix() {
		return propertyI18nPrefix;
	}
}