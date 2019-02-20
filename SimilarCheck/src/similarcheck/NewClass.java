/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package similarcheck;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.util.Iterator;
import javax.management.Query;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import static sun.rmi.transport.TransportConstants.Version;



 public class NewClass {
                String indexDir = "H:\\index" ;
                String dataDir = "H:\\Word\\" ;

//            public static void main(String[] arg) {
////                splitDataToSaveFile(1, "H:\\word.txt", "H:\\Word\\");
//                NewClass tester ;
//                        try{
//                                tester = new NewClass();
////                                tester.index();i
//                                ArrayList<String> al=tester.search("appl"); 
//                                Iterator<String> iterator = al.iterator();
//                                while(iterator.hasNext()){
//                                     System.out.println(iterator.next());
//                                }
//                        }catch(Exception ex){
//                                ex.printStackTrace();
//                        }
//            }

    //将txt文件按行切分成若干个文件
            public static void splitDataToSaveFile(int rows, String sourceFilePath, String targetDirectoryPath) {
                        File sourceFile = new File(sourceFilePath);
                        File targetFile = new File(targetDirectoryPath);
                        if (!sourceFile.exists() || rows <= 0 || sourceFile.isDirectory()) {
                                return;
                        }
                        if (targetFile.exists()) {
                                if (!targetFile.isDirectory()) {
                                        return;
                                }
                        } else {
                                targetFile.mkdirs();
                        }
                        try {
                                InputStreamReader in = new InputStreamReader(new FileInputStream(sourceFilePath), "GBK");
                                BufferedReader br = new BufferedReader(in);
                                BufferedWriter bw = null;
                                String str = "";
                                String tempData = br.readLine();
                                int i = 1, s = 0;
                                while (tempData != null) {
                                        str += tempData + "\r\n";
                                        if (i % rows == 0) {
                                                bw = new BufferedWriter(new OutputStreamWriter(
                                                                new FileOutputStream(targetFile.getAbsolutePath() + "/" + sourceFile.getName() + "_" + (s + 1) + ".txt"), "UTF-8"), 1024);

                                                bw.write(str);
                                                bw.close();
                                                str = "";
                                                s += 1;
                                        }
                                        i++;
                                        tempData = br.readLine();
                                }
        //			if ((i - 1) % rows != 0) {
        //				bw = new BufferedWriter(
        //						new OutputStreamWriter(
        //								new FileOutputStream(targetFile.getAbsolutePath() + "/" + sourceFile.getName() + "_" + (s + 1) + ".txt"), "UTF-8"),
        //						1024);
        //				bw.write(str);
        //				bw.close();
        //				br.close();
        //				s += 1;
        //			}
                                in.close();
                        } catch (Exception e) {
                        }
                }
            
            public void index() {  
        IndexWriter indexWriter = null;  
        try {  
            // 创建Directory  
            Directory directory = FSDirectory.open(Paths.get(indexDir));  
  
            // 创建IndexWriter  
           Analyzer analyzer = new StandardAnalyzer() ;
           IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(directory, iwc);  
  
            File dFile = new File(dataDir);
            File[] files = dFile.listFiles();
            for (File file : files) {
                Document document = new Document();
  
                // 为Document添加Field
                document.add(new Field("content", new FileReader(file), TextField.TYPE_NOT_STORED));  
                document.add(new Field("filename", file.getName(), TextField.TYPE_STORED));  
                document.add(new Field("filepath", file.getAbsolutePath(), TextField.TYPE_STORED));  
  
                // 通过IndexWriter添加文档到索引中  
                indexWriter.addDocument(document);  
            }
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (indexWriter != null) {  
                    indexWriter.close();  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }
    }

            public ArrayList<String> search(String con) {  
                ArrayList<String> wordArrayList=new ArrayList<String>();
            DirectoryReader directoryReader = null;  
        try {  
             
            
            Directory directory = FSDirectory.open(Paths.get(indexDir));  
            // 创建IndexReader  
            directoryReader = DirectoryReader.open(directory);  
            // 根据IndexReader创建IndexSearch
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);  
            // 创建搜索的Query  
            Analyzer analyzer = new StandardAnalyzer();  
            QueryParser queryParser = new QueryParser("content", analyzer);   
            // 万能索引 
            org.apache.lucene.search.Query query = queryParser.parse(con);  
            //模糊索引
            Term t = new Term("content",con);
            FuzzyQuery query1 = new FuzzyQuery(t);
            TopDocs topDocs = indexSearcher.search(query1, 10);  
            // 根据TopDocs获取ScoreDoc对象  
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;  
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 根据searcher和ScoreDoc对象获取具体的Document对象  
                Document document = directoryReader.document(scoreDoc.doc);
                // 根据Document对象获取需要的值  
                 File file = new File(document.get("filepath"));
                 InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
                 BufferedReader br = new BufferedReader(isr);
                 wordArrayList.add(br.readLine());
            }
            
           
        } catch (Exception e) {
            e.printStackTrace();  
        } finally {  
            try {  
                if (directoryReader != null) {  
                    directoryReader.close();  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
         return wordArrayList;
            }
}


	


    
