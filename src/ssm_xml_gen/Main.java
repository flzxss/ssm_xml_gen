package ssm_xml_gen;

import cn.hutool.setting.Setting;

public class Main {

	public static void main(String[] args) {
		Setting setting = new Setting("config/conf.properties");
		String str = setting.getStr("name");
		System.out.println(str);
		String code = "List<String> add(List<Integer>list,String username);//添加一行代码";
		CodeGen.gen(code);
	}

}
