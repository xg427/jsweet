/* 
 * TypeScript definitions to Java translator - http://www.jsweet.org
 * Copyright (C) 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jsweet.input.typescriptdef.visitor;

import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetDefTranslatorConfig;
import org.jsweet.input.typescriptdef.ast.Context;
import org.jsweet.input.typescriptdef.ast.FunctionDeclaration;
import org.jsweet.input.typescriptdef.ast.Scanner;
import org.jsweet.input.typescriptdef.ast.Type;
import org.jsweet.input.typescriptdef.ast.TypeDeclaration;
import org.jsweet.input.typescriptdef.ast.TypeReference;
import org.jsweet.input.typescriptdef.ast.VariableDeclaration;

/**
 * This scanner substitutes void return types of parent methods with Object.
 * 
 * <p>
 * In Typescript, one can override a void method with a method that returns an
 * object. In Java, it is not possible. So this scanner make sure that the
 * parent method with actually return an object to avoid Java compile errors.
 * 
 * @author Renaud Pawlak
 */
public class ParentMethodReturnTypeSusbtitutor extends Scanner {

	public ParentMethodReturnTypeSusbtitutor(Context context) {
		super(context);
	}

	private void applyToParentMethod(TypeDeclaration declaringType, FunctionDeclaration childFunction,
			TypeDeclaration parentType, Consumer<FunctionDeclaration> apply) {
		int index = -1;
		if (declaringType != parentType) {
			index = ArrayUtils.indexOf(parentType.getMembers(), childFunction);
		}
		if (index != -1) {
			apply.accept((FunctionDeclaration) parentType.getMembers()[index]);
		} else {
			if (parentType.getSuperTypes() != null && parentType.getSuperTypes().length > 0) {
				for (TypeReference ref : parentType.getSuperTypes()) {
					Type decl = lookupType(ref, null);
					if (decl instanceof TypeDeclaration) {
						applyToParentMethod(declaringType, childFunction, (TypeDeclaration) decl, apply);
					}
				}
			} else if (!JSweetDefTranslatorConfig.getObjectClassName().equals(context.getTypeName(parentType))) {
				TypeDeclaration decl = context.getTypeDeclaration(JSweetDefTranslatorConfig.getObjectClassName());
				if (decl != null) {
					applyToParentMethod(declaringType, childFunction, (TypeDeclaration) decl, apply);
				}
			}
		}
	}

	@Override
	public void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
		TypeDeclaration declaringType = getParent(TypeDeclaration.class);
		if (declaringType != null && functionDeclaration.getType() != null
				&& functionDeclaration.getType().getName() != null
				&& !functionDeclaration.getType().getName().equals("void")) {
			applyToParentMethod(
					declaringType,
					functionDeclaration,
					declaringType,
					(function) -> {
						if (functionDeclaration.getType().isPrimitive() && !function.getType().isPrimitive()) {
							functionDeclaration.getType().setName(
									StringUtils.capitalize(functionDeclaration.getType().getName()));
						} else {
							if ("void".equals(function.getType().getName())) {
								System.out.println("modify return type of " + function + ": " + function.getType()
										+ " -> " + functionDeclaration.getType());
								function.setType(functionDeclaration.getType());
							}
						}
					});
		}
	}

	@Override
	public void visitVariableDeclaration(VariableDeclaration variableDeclaration) {
	}

}
