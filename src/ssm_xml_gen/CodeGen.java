package ssm_xml_gen;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

public class CodeGen {

	public static void gen(String code) {
		code = code.trim();
		String codeMajor = StrUtil.subBefore(code, "//", true);
		String comment = StrUtil.subAfter(code, "//", true);
		String bracketsLeft = StrUtil.subBefore(codeMajor, "(", true);
		String bracketsRight = StrUtil.subAfter(codeMajor, "(", true);
		String methodBody = StrUtil.subBefore(bracketsRight, ")", true);
		comment = comment.trim();
		bracketsLeft = bracketsLeft.trim();
		methodBody = methodBody.trim();
		bracketsLeft = StrUtil.removePrefix(bracketsLeft, "public");
		bracketsLeft = StrUtil.removePrefix(bracketsLeft, "private");
		bracketsLeft = StrUtil.removePrefix(bracketsLeft, "protected");
		bracketsLeft = bracketsLeft.trim();
		bracketsLeft = StrUtil.removePrefix(bracketsLeft, "static");
		bracketsLeft = bracketsLeft.trim();
		String returnBody = StrUtil.subBefore(bracketsLeft, " ", true);
		String methodName = StrUtil.subAfter(bracketsLeft, " ", true);
		returnBody = returnBody.trim();
		methodName = methodName.trim();
		List<String> paramList = StrUtil.splitTrim(methodBody, ",");
		String now = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		System.out.println("comment=" + comment);
		System.out.println("returnBody=" + returnBody);
		System.out.println("methodName=" + methodName);
//        System.out.println("methodBody="+methodBody);
		System.out.println("paramList=" + paramList);
		System.out.println(Arrays.toString(analysisParam(paramList.get(0))));
		System.out.println(Arrays.toString(analysisParam(paramList.get(1))));
		String serviceCode = genService(methodName, returnBody, paramList, comment, now);
		String mapperCode = genMapper(methodName, returnBody, paramList, comment, now);
		System.out.println("serviceCode=");
		System.out.println(serviceCode);
		System.out.println("mapperCode=");
		System.out.println(mapperCode);
		// String serviceCode = genService(methodName,returnBody,paramList,comment,now);
//        System.out.println("="+);
//        System.out.println("="+);
	}

	private static String genMapper(String methodName, String returnBody, List<String> paramList, String comment,
			String now) {
		String tab = "    ";
		String nl = "\n";
		String sum = "";
		String exception = "throws Exception ";
		sum += tab + tab + "/** " + nl;
		sum += tab + tab + " * " + comment + "" + nl;
		sum += tab + tab + " * " + "@version" + " " + now + nl;
		sum += tab + tab + " */" + nl;
		sum += tab + tab + "public " + returnBody + " " + methodName + "(" + StrUtil.join(",", paramList) + ") "
				+ exception + "{" + nl;
		sum += tab + tab + "Map<String,Object> map = new HashMAp<String,Object>();" + nl;
		for (int i = 0; i < paramList.size(); i++) {
			String row = paramList.get(i);
			String[] analysisParam = analysisParam(row);
			String name = analysisParam[0];
			String typeBase = analysisParam[1];
			String typeGeneric = analysisParam[2];
			String type = typeBase;
			if (typeGeneric != null && !"Map".equals(typeBase)) {
				type = typeGeneric;
			}
			String key = name;
			String value = name;
			sum += tab + tab + tab + "map.put(\"" + key + ", " + "" + value + "" + ");" + nl;
		}
		String filebaseName = "BaseMapper";
		sum += tab + tab + tab + returnBody + " result" + " = " + "dao." + currencyMethod(methodName, returnBody) + "("
				+ paramService(filebaseName, methodName) + ");" + nl;
		sum += tab + tab + tab + "return result;" + nl;
		sum += tab + tab + "}" + nl;
		sum += "" + nl;
		return sum;
	}

	private static String paramService(String filebaseName, String methodName) {
		String result = "\"" + filebaseName + "." + methodName + "\"" + ", " + "map";
		return result;
	}

	private static String currencyMethod(String methodName, String returnBody) {
		if (methodName.startsWith("save")) {
			return "save";
		}
		if (methodName.startsWith("get")) {
			String[] analysisParam = analysisParam(returnBody);
			String name = analysisParam[0];
			String typeBase = analysisParam[1];
			String typeGeneric = analysisParam[2];
			String type = typeBase;
			if (typeGeneric != null && !"Map".equals(typeBase)) {
				type = typeGeneric;
			}
			if ("List".equals(type)) {
				return "selectList";
			}
			if ("Map".equals(type)) {
				return "selectMap";
			}
			return "selectObject";
		}
		if (methodName.startsWith("update")) {
			return "update";
		}
		if (methodName.startsWith("delete")) {
			return "update";
		}
		return "select";
	}

	private static String genService(String methodName, String returnBody, List<String> paramList, String comment,
			String now) {
		String tab = "    ";
		String nl = "\n";
		String sum = "";
		String nodeName = xmlName(methodName);
		String id = id(methodName);
		sum += tab + tab + "<!-- " + comment + " -->" + nl;
		sum += tab + tab + "<" + nodeName + " " + id + " " + parameterType(paramList) + " " + resultType(returnBody)
				+ ">" + nl;
		for (int i = 0; i < paramList.size(); i++) {
			String row = paramList.get(i);
			String[] analysisParam = analysisParam(row);
			String name = analysisParam[0];
			String typeBase = analysisParam[1];
			String typeGeneric = analysisParam[2];
			String type = typeBase;
			if (typeGeneric != null && !"Map".equals(typeBase)) {
				type = typeGeneric;
			}
			String key = name;
			String value = name;
			sum += tab + tab + tab + "#{" + key + "}" + nl;
		}
//		sum += tab + tab + "" + nl;
		sum += tab + tab + "</" + nodeName + ">" + nl;
		return sum;
	}

	private static String id(String methodName) {
		String result = "id=\"" + methodName + "\"";
		return result;
	}

	private static String resultType(String str) {
		String[] analysisParam = analysisParam(str);
		String name = analysisParam[0];
		String typeBase = analysisParam[1];
		String typeGeneric = analysisParam[2];
		String type = typeBase;
		if (typeGeneric != null && !"Map".equals(typeBase)) {
			type = typeGeneric;
		}
		String fullNameType = fullNameType(type);
		String result = "resultType=\"" + fullNameType + "\"";
		return result;
	}

	private static String fullNameType(String type) {
		if ("String".equals(type)) {
			return "java.lang.String";
		}
		if ("Integer".equals(type)) {
			return "java.lang.Integer";
		}
		if ("Map".equals(type)) {
			return "java.util.Map";
		}
		if ("java.util.Map".equals(type)) {
			return "java.util.Map";
		}
		return "java.util.Map";
	}

	private static String parameterType(List<String> paramList) {
		String type = "java.util.Map";
		if (paramList.size() > 1) {
			type = "java.util.Map";
		} else {
			String first = CollUtil.getFirst(paramList);
			String[] analysisParam = analysisParam(first);
			String name = analysisParam[0];
			String typeBase = analysisParam[1];
			String typeGeneric = analysisParam[2];
			type = typeBase;
			if (typeGeneric != null && !"Map".equals(typeBase)) {
				type = typeGeneric;
			}
		}
		String parameterType = fullNameType(type);
		String result = "parameterType=\"" + parameterType + "\"";
		return result;
	}

	private static String xmlName(String methodName) {
		if (methodName.startsWith("save")) {
			return "insert";
		}
		if (methodName.startsWith("get")) {
			return "select";
		}
		if (methodName.startsWith("update")) {
			return "update";
		}
		if (methodName.startsWith("delete")) {
			return "update";
		}
		return "select";
	}

	private static String[] analysisParam(String str) {
		String[] param = new String[3];
		if (str.contains(" ")) {
			String type = StrUtil.subBefore(str, " ", true);
			String name = StrUtil.subAfter(str, " ", true);
			type = type.trim();
			name = name.trim();
			if (type.contains("<")) {
				String typeBase = StrUtil.subBefore(type, "<", true);
				String typeGeneric = StrUtil.subAfter(type, "<", true);
				typeBase = typeBase.trim();
				typeGeneric = typeGeneric.trim();
				typeGeneric = StrUtil.removeSuffix(typeGeneric, ">");
				typeGeneric = typeGeneric.trim();
				param[0] = name;
				param[1] = typeBase;
				param[2] = typeGeneric;
			} else {
				param[0] = name;
				param[1] = type;
			}
		} else if (str.contains(">")) {
			String type = StrUtil.subBefore(str, ">", true);
			String name = StrUtil.subAfter(str, ">", true);
			type = type.trim();
			name = name.trim();
			if (type.contains("<")) {
				String typeBase = StrUtil.subBefore(type, "<", true);
				String typeGeneric = StrUtil.subAfter(type, "<", true);
				typeBase = typeBase.trim();
				typeGeneric = typeGeneric.trim();
				typeGeneric = StrUtil.removeSuffix(typeGeneric, ">");
				typeGeneric = typeGeneric.trim();
				param[0] = name;
				param[1] = typeBase;
				param[2] = typeGeneric;
			}
		}
		return param;
	}

}
