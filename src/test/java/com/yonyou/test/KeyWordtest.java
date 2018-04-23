package com.yonyou.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.jfinal.kit.PropKit;

public class KeyWordtest {

	public static void main(String[] args) throws IOException {
		PropKit.use("config.properties");
		String logPath = PropKit.get("filepath");
		File dir = new File(logPath);
		if (!dir.exists()) {
			logPath = System.getProperty("user.dir");
		}
		String filePath = logPath + "\\file\\test-utf8.TXT";
		String tt = new String();
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		String str;
		while ((str = in.readLine()) != null) {
			tt += str;
		}
		//test1(tt);
		//nlpAnalysis(tt);
		addDel(tt);
	}
	
	
	/**
	 * 自定义分词方案
	 * @param content
	 */
	public static void addDel(String content){
		UserDefineLibrary.insertWord("ansj中文分词", "userDefine", 1000);
        List<Term> terms = ToAnalysis.parse(content);
        System.out.println("增加新词例子:" + terms);
        // 删除词语,只能删除.用户自定义的词典.
        UserDefineLibrary.removeWord("ansj中文分词");
        terms = ToAnalysis.parse("我觉得ansj中文分词是一个不错的系统!我是王婆!");
        System.out.println("删除用户自定义词典例子:" + terms);
	}

	public static void test1(String content) {
		KeyWordComputer key = new KeyWordComputer(30);
		Iterator<Keyword> it = key.computeArticleTfidf(content).iterator();
		while (it.hasNext()) {
			Keyword key2 = (Keyword) it.next();
			System.out.println(key2.toString() + "----" +key2.getScore()+ "----" +key2.getFreq());
		}
	}
	
       
	/**
	 * 基本分词方式
	 * @param content
	 */
	public static void base(String content) {
		List<Term> parse = BaseAnalysis.parse(content);
		System.out.println(parse);
	}

	/**
	 * 精准分词方式兼顾精度与速度，比较均衡；
	 * 
	 * @param content
	 */
	public static void toAnalysis(String content) {
		List<Term> parse = ToAnalysis.parse(content);
		System.out.println(parse);
	}

	/**
	 * NLP分词方式可是未登录词，但速度较慢
	 * 
	 * 
	 * @param content
	 */
	public static void nlpAnalysis(String content) {
		List<Term> parse = NlpAnalysis.parse(content);
		for(Term term:parse){
			System.out.println(term.getName() + "--" + term.getScore() + "--" + term.getTermNatures().toString());
		}
	}

	/**
	 * 面向索引方式适合用在在lucene等文本检索中用到
	 * 
	 * @param content
	 */
	public static void toLucene(String content) {
		List<Term> parse = IndexAnalysis.parse(content);
		System.out.println(parse);
	}

}
