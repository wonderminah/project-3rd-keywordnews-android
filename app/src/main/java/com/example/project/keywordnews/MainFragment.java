package com.example.project.keywordnews;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by SCIT on 2018-03-07.
 */

public class MainFragment extends android.support.v4.app.Fragment {

    //멤버변수 선언.
    ListView newsListView;
    ListViewAdapter adapter;
    String page;
    String html;
    ArrayList<News> newsArrayList;

    //Mandatory Constructor: 프래그먼트는 항상 기본 생성자가 있어야 한다.
    public MainFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d("route log", "MainFragment > onCreateView");

        //MainActivity에서 보내온 page 값을 가져온다.
        try {
            page = getArguments().getString("page", "http://news.sbs.co.kr/news/SectionRssFeed.do?sectionId=01");
        }
        catch (Exception e) {
            page = "http://news.sbs.co.kr/news/SectionRssFeed.do?sectionId=01";
        }
        Log.d("route log", "page: " + page);

        //fragment_main.xml을 레이아웃으로 적용한다.
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_main, container, false);

        //newsListView를 받아서 Adapter를 달아준다. (알고보니 여기서 안달고 onPostExcuted까지 하고나서 달아야 했음.)
        newsListView = (ListView) layout.findViewById(R.id.newsListView);

        //파싱 작업으로 들어간다.
        new MyTask().execute();

        return layout;
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("route log", "MainFragment > MyTask > doInBackground()");

            try {
                Log.d("debug", "MainFragment > MyTask > doInBackground() > read 'page' and return 'html'"); //로그

                //String page = "http://news.sbs.co.kr/news/SectionRssFeed.do?sectionId=01";

                URL url = new URL(page);
                HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();

                if(urlConnection == null) return null;
                urlConnection.setConnectTimeout(10000); //최대 10초 대기
                urlConnection.setUseCaches(false); //매번 서버에서 읽어오기
                StringBuilder sb = new StringBuilder(); //고속 문자열 결합체

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");

                    //한줄씩 읽기
                    BufferedReader br = new BufferedReader(isr);
                    while(true) {
                        String line = br.readLine(); //웹페이지의 html 코드 읽어오기
                        if (line == null) break; //스트림이 끝나면 null리턴
                        sb.append(line+"\n");
                    } //end while
                    br.close();
                } //end if
                html = sb.toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("debug", "MainFragment > MyTask > doInBackground() " + html);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("route log", "MainFragment > MyTask > onPostExecute()");

            newsArrayList = new ArrayList<>();

            try {
                Log.d("debug", "XMLParsing try_html: " + html);

                //DOM 파싱.
                ByteArrayInputStream bai = new ByteArrayInputStream(html.getBytes());
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                //dbf.setIgnoringElementContentWhitespace(true); //화이트스패이스 생략
                DocumentBuilder builder = dbf.newDocumentBuilder();
                Document parse = builder.parse(bai);//DOM 파서

                //<item>이라는 태그 단위의 정보들을 하나하나씩 datas라는 NodeList에 담는다.
                NodeList datas = parse.getElementsByTagName("item");
                Log.d("route log", "datas.getLength(): " + datas.getLength()); //49개 맞게 나옴.     //L

                String result = "";
                //부모태그 (<item>)
                for (int idx = 0; idx < datas.getLength(); idx++) {
                    String title = "";
                    String link = "";
                    String description = "";
                    String category = "";
                    String imgSrc = "";
                    String pubDate = "";
                    String author = "";
                    Node node = datas.item(idx);
                    int childLength = node.getChildNodes().getLength();
                    Log.d("debug", "childLength: " + childLength);

                    //자식태그 (<title>, <link>, <description> ...)
                    NodeList childNodes = node.getChildNodes();
                    for (int childIdx = 0; childIdx < childLength; childIdx++) {
                        Node childNode = childNodes.item(childIdx);
                        Log.d("debug", "childNode" + childIdx + ": " + childNode.getNodeName());
                        if(childNode.getNodeType() == Node.ELEMENT_NODE){
                            String tag = childNode.getNodeName();
                            switch (tag) {
                                case "title": title = childNode.getFirstChild().getNodeValue(); break;
                                case "link": link = childNode.getFirstChild().getNodeValue(); break;
                                case "description": description = childNode.getFirstChild().getNodeValue(); break;
                                case "category": if (category.equals("")) {category = childNode.getFirstChild().getNodeValue();} break;
                                case "enclosure": imgSrc = childNode.getAttributes().getNamedItem("url").getNodeValue(); break;
                                case "pubDate": pubDate = childNode.getFirstChild().getNodeValue(); break;
                                case "author": author = childNode.getFirstChild().getNodeValue(); break;
                            }
                        }
                    } //end 안쪽 for문
                    description = Html.fromHtml(description).toString().trim(); //description의 공백 제거, 정규식 표현 제거.

                    result += "title: " + title + "\n" + "link: " + link + "\n" + "description: " + description + "\n" + "category: " + category + "\n" + "imgSrc: " + imgSrc + "\n" + "pubDate: " + pubDate + "\n" + "author: " + author + "\n";
                    Log.d("route log: ", "result: " + result);

                    News news = new News();
                    news.setTitle(title);
                    news.setLink(link);
                    news.setPubDate(pubDate);
                    news.setAuthor(author);
                    news.setCategory(category);
                    news.setDescription(description);
                    news.setImgSrc(imgSrc);
                    newsArrayList.add(news);
                } //end 바깥쪽 for문
            }
            catch (Exception e) {
                Log.d("route log", "XMLParsing catch");
                Log.e("route log", "XMLParsing error" + e.toString());
                e.printStackTrace();
            }
            addToAdapter(newsArrayList);
        }
    }

    public void addToAdapter(ArrayList<News> newsArrayList) {

        //listView에 접근할 Adapter 생성
        adapter = new ListViewAdapter();

        Log.d("route log", "MainFragment > addToAdapter");
        Log.d("route log", "newsArrayList: " + newsArrayList.toString());
        Log.d("route log", "adapter: " + adapter);

        //반복문을 통해 어댑터에 뉴스기사 리스트 추가 (출처: http://recipes4dev.tistory.com/43 [개발자를 위한 레시피])
        for (int i = 0; i < newsArrayList.size(); i++) {
            Log.d("route log", "title: " + newsArrayList.get(i).getTitle() + " description: " + newsArrayList.get(i).getDescription());
            adapter.addItem(newsArrayList.get(i).getImgSrc(), newsArrayList.get(i).getTitle(), newsArrayList.get(i).getDescription());
        }
        //잘 들어왔는지 테스트 로그
        for (int i = 0; i < adapter.getCount(); i++) {
            Log.d("route log", "adapter List: " + adapter.getItem(i).toString());
        }

        //어댑터를 통해 리스트뷰에 연결
        newsListView.setAdapter(adapter);

        //기사 리스트뷰 아이템을 클릭 / 롱클릭 시 웹뷰를 나타낼 핸들러로 연결
        NewsClickHandler nh = new NewsClickHandler();
        newsListView.setOnItemClickListener(nh);
        NewsLongClickHandler lc = new NewsLongClickHandler();
        newsListView.setOnItemLongClickListener(lc);
    }

    //뉴스기사 클릭 시 웹뷰 출력
    private class NewsClickHandler implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            //테스트용 로그
            Log.d("route log", "MainFragment > NewsClickHanler > onItemClick() > " +
                    "position: " + position + "\n" +
                    "adapterView: " + adapterView + "\n" +
                    "view: " + view + "\n" +
                    "long l: " + l);

            //클릭한 기사의 링크
            String link = newsArrayList.get(position).getLink();

            //링크를 웹뷰로 출력
            Intent intent = new Intent(getContext(), Activity_Webview.class);
            intent.putExtra("link", link); //기사의 링크 주소를 값으로 집어넣음.
            startActivity(intent); //webview를 실행함.
        }
    }

    //뉴스기사 롱클릭 시 페이스북 공유
    private class NewsLongClickHandler implements ListView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("route log", "MainFragment > NewsLongClickHanler > onItemLongClick() > int position: " + position);

            ShareLinkContent shareContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(newsArrayList.get(position).getLink()))
                    .build();
            ShareDialog shareDialog = new ShareDialog(MainFragment.this);
            shareDialog.show(shareContent, ShareDialog.Mode.FEED);

            return true; //true를 해야 onClick 이벤트와 동시에 실행되지 않는다. >> 안되는데??
        }
    }
}
