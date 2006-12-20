/*
 * Copyright 2003-2006 Dave Griffith, Bas Leijdekkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ipp.forloop;

import com.siyeh.ipp.base.Intention;
import com.siyeh.ipp.base.PsiElementPredicate;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;

public class ReplaceForEachLoopWithIteratorForLoopIntention extends Intention {

    @NotNull
    public PsiElementPredicate getElementPredicate() {
        return new IterableForEachLoopPredicate();
    }

    public void processIntention(@NotNull PsiElement element)
            throws IncorrectOperationException {
        final PsiForeachStatement statement =
                (PsiForeachStatement)element.getParent();
        if (statement == null) {
            return;
        }
        final PsiManager psiManager = statement.getManager();
        final Project project = psiManager.getProject();
        final CodeStyleManager codeStyleManager =
                CodeStyleManager.getInstance(project);
        final PsiExpression iteratedValue = statement.getIteratedValue();
        if (iteratedValue == null) {
            return;
        }
        final PsiType type = iteratedValue.getType();
        final PsiType iteratedValueParameterType;
        if (type instanceof PsiClassType) {
            final PsiClassType classType = (PsiClassType) type;
            final PsiType[] parameterTypes = classType.getParameters();
            if(parameterTypes.length == 0) {
                iteratedValueParameterType = null;
            } else {
                iteratedValueParameterType = parameterTypes[0];
            }
        } else {
            iteratedValueParameterType = null;
        }
        @NonNls final StringBuilder newStatement = new StringBuilder();
        final PsiParameter iterationParameter =
                statement.getIterationParameter();
        final PsiType parameterType = iterationParameter.getType();
        final String iterator =
                codeStyleManager.suggestUniqueVariableName(
                        "it", statement, true);
        final String typeText = parameterType.getCanonicalText();
        newStatement.append("for(java.util.Iterator");
        if (iteratedValueParameterType == null) {
            newStatement.append(' ');
        } else {
            newStatement.append('<');
            newStatement.append(iteratedValueParameterType.getCanonicalText());
            newStatement.append("> ");
        }
        newStatement.append(iterator);
        newStatement.append(" = ");
        newStatement.append(iteratedValue.getText());
        newStatement.append(".iterator();");
        newStatement.append(iterator);
        newStatement.append(".hasNext();) {");
        newStatement.append(typeText);
        newStatement.append(' ');
        newStatement.append(iterationParameter.getName());
        newStatement.append(" = ");
        if (iteratedValueParameterType == null &&
                !"java.lang.Object".equals(typeText)) {
            newStatement.append('(');
            newStatement.append(typeText);
            newStatement.append(')');
        }
        newStatement.append(iterator);
        newStatement.append(".next();");
        final PsiStatement body = statement.getBody();
        if (body == null) {
            return;
        }
        if (body instanceof PsiBlockStatement) {
            final PsiCodeBlock block =
                    ((PsiBlockStatement)body).getCodeBlock();
            final PsiElement[] children = block.getChildren();
            for (int i = 1; i < children.length - 1; i++) {
                //skip the braces
                newStatement.append(children[i].getText());
            }
        } else {
            newStatement.append(body.getText());
        }
        newStatement.append('}');
        replaceStatementAndShorten(newStatement.toString(), statement);
    }
}