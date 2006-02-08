/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.internal.databinding;

import org.eclipse.jface.databinding.BindingEvent;
import org.eclipse.jface.databinding.BindingException;
import org.eclipse.jface.databinding.ChangeEvent;
import org.eclipse.jface.databinding.IBindSpec;
import org.eclipse.jface.databinding.IChangeListener;
import org.eclipse.jface.databinding.IUpdatableValue;
import org.eclipse.jface.databinding.converter.IConverter;
import org.eclipse.jface.databinding.validator.IDomainValidator;
import org.eclipse.jface.databinding.validator.IValidator;

/**
 * @since 3.2
 * 
 */
public class ValueBinding extends Binding {

	private final IUpdatableValue target;

	private final IUpdatableValue model;

	private IValidator validator;

	private IConverter converter;

	private IDomainValidator domainValidator;

	private boolean updating = false;

	/**
	 * @param context
	 * @param target
	 * @param model
	 * @param bindSpec
	 */
	public ValueBinding(DataBindingContext context, IUpdatableValue target,
			IUpdatableValue model, IBindSpec bindSpec) {
		super(context);
		this.target = target;
		this.model = model;
		converter = bindSpec.getConverter();
		if (converter == null) {
			throw new BindingException("Missing converter from " + target.getValueType() + " to " + model.getValueType()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!converter.getModelType().isAssignableFrom(model.getValueType())) {
			throw new BindingException(
					"Converter does not apply to model type. Expected: " + model.getValueType() + ", actual: " + converter.getModelType()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!converter.getTargetType().isAssignableFrom(target.getValueType())) {
			throw new BindingException(
					"Converter does not apply to target type. Expected: " + target.getValueType() + ", actual: " + converter.getTargetType()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		validator = bindSpec.getValidator();
		if (validator == null) {
			throw new BindingException("Missing validator"); //$NON-NLS-1$
		}
		domainValidator = bindSpec.getDomainValidator();
		target.addChangeListener(targetChangeListener);
		model.addChangeListener(modelChangeListener);
	}

	private final IChangeListener targetChangeListener = new IChangeListener() {
		public void handleChange(ChangeEvent changeEvent) {
			if (updating) 
				return;
			if (changeEvent.getChangeType() == ChangeEvent.VERIFY) {
				// we are notified of a pending change, do validation
				// and veto the change if it is not valid
				Object value = changeEvent.getNewValue();
				String partialValidationError = validator
						.isPartiallyValid(value);
				context.updatePartialValidationError(this,
						partialValidationError);
				if (partialValidationError != null) {
					changeEvent.setVeto(true);
				}
			} else if (changeEvent.getChangeType() == ChangeEvent.CHANGE) {
				// the target (usually a widget) has changed, validate
				// the value and update the source
				updateModelFromTarget(changeEvent);
			}
		}
	};
	
	private IChangeListener modelChangeListener = new IChangeListener() {
		public void handleChange(ChangeEvent changeEvent) {
			if (updating) 
				return;
			// The model has changed so we must update the target
			if (changeEvent.getChangeType() == ChangeEvent.VERIFY) {
			} else if (changeEvent.getChangeType() == ChangeEvent.CHANGE) {
				updateTargetFromModel(changeEvent);
			}
		}
	};
	

	/**
	 * This also does validation.
	 * @param changeEvent TODO
	 */
	public void updateModelFromTarget(ChangeEvent changeEvent) {
		BindingEvent e = new BindingEvent(changeEvent, BindingEvent.EVENT_COPY_TO_MODEL, BindingEvent.PIPELINE_AFTER_GET);
		e.originalValue = target.getValue();
		if (failure(errMsg(fireBindingEvent(e)))) {
			return;
		}
		
		String validationError = doValidate(e.originalValue);
		if (validationError != null) {
			return;
		}
		e.pipelinePosition = BindingEvent.PIPELINE_AFTER_VALIDATE;
		if (failure(errMsg(fireBindingEvent(e)))) {
			return;
		}
		
		try {
			updating = true;
			
			e.convertedValue = converter.convertTargetToModel(e.originalValue);
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_CONVERT;
			if (failure(errMsg(fireBindingEvent(e)))) {
				return;
			}
			
			validationError = doDomainValidation(e.convertedValue);
			if (validationError != null) {
				return;
			}
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_BUSINESS_VALIDATE;
			if (failure(errMsg(fireBindingEvent(e)))) {
				return;
			}
			
			model.setValue(e.convertedValue);
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_CHANGE;
			fireBindingEvent(e);
		} catch (Exception ex) {
			context.updateValidationError(targetChangeListener, BindingMessages
					.getString("ValueBinding_ErrorWhileSettingValue")); //$NON-NLS-1$
		} finally {
			updating = false;
		}
	}

	/**
	 * @param convertedValue
	 * @return String
	 */
	private String doDomainValidation(Object convertedValue) {
		if (domainValidator == null) {
			return null;
		}
		String validationError = domainValidator.isValid(convertedValue);
		return errMsg(validationError);
	}

	private String doValidate(Object value) {
		String validationError = validator.isValid(value);
		return errMsg(validationError);
	}

	private String errMsg(String validationError) {
		context.updatePartialValidationError(targetChangeListener, null);
		context.updateValidationError(targetChangeListener, validationError);
		return validationError;
	}
	
	private boolean failure(String errorMessage) {
		if (errorMessage != null) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.internal.databinding.Binding#updateTargetFromModel()
	 */
	public void updateTargetFromModel(ChangeEvent changeEvent) {
		try {
			updating = true;
			BindingEvent e = new BindingEvent(changeEvent, BindingEvent.EVENT_COPY_TO_TARGET, BindingEvent.PIPELINE_AFTER_GET);
			e.originalValue = model.getValue();
			if (failure(errMsg(fireBindingEvent(e)))) {
				return;
			}
			
			e.convertedValue = converter.convertModelToTarget(e.originalValue);
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_CONVERT;
			if (failure(errMsg(fireBindingEvent(e)))) {
				return;
			}
			
			target.setValue(e.convertedValue);
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_CHANGE;
			if (failure(errMsg(fireBindingEvent(e)))) {
				return;
			}
			
			doValidate(target.getValue());
			e.pipelinePosition = BindingEvent.PIPELINE_AFTER_VALIDATE;
			fireBindingEvent(e);
		} finally {
			updating = false;
		}
	}
}