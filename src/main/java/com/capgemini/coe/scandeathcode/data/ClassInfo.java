package com.capgemini.coe.scandeathcode.data;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.Type;

public class ClassInfo {

	private String packageName;
	private String name;

	private List<String> imports;

	private List methods;

	public ClassInfo(String packageName) {
		imports = new ArrayList<>();

		this.packageName = packageName;
	}

	public void setName(String name) {
		this.name = name;

	}

	public String getPackageName() {
		return packageName;
	}

	public void addImport(String importName) {
		if (this.imports.contains(importName))
			return;

		this.imports.add(importName);
	}

	public String getName() {
		return name;
	}

	public void addMethod(Modifier modifier, SimpleName methodName, Type type, NodeList<Parameter> parameters) throws ClassNotFoundException {

		System.out.println(modifier + " : " + findClass(type) + " : " + methodName);

		if (methodName.toString().equals("addGroupToJenkinsScreen")) {

			for (Parameter parameter : parameters) {
				System.out.println("\t" + findClass(parameter.getType()));
			}
		}

	}

	public String findClass(Type type) {

		String name = "";

		if (type.isArrayType())
			name = "List-";

		for (String importName : imports) {
			if (importName.endsWith(type.asString()))
				return name + importName;
		}

		return name + type.asString();
	}

}
