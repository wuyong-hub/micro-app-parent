package com.wysoft.https_auth;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.springframework.util.StringUtils;

import com.wysoft.https_base.util.HttpUtils;

import net.sf.json.JSONObject;

public class HttpPostTestWindow extends JFrame {

	public HttpPostTestWindow(String title) {
		JFrame jf = new JFrame(title);
		JPanel pane = new JPanel();
		jf.setContentPane(pane);
		jf.setLayout(null);
		JLabel L1 = new JLabel("URL地址：");
		L1.setBounds(10, 10, 80, 30);
		final JTextField t1 = new JTextField("https://localhost:8443/httpServiceEntry"); // 创建一个标签 并设置初始内容
		t1.setBounds(100, 10, 300, 30);
		JComboBox<String> c1 = new JComboBox<>(new String[] {"Post","Get"});
		c1.setBounds(410, 10, 80, 30);
		JButton jb = new JButton("发送");
		jb.setBounds(500, 10, 80, 30);
		JButton jb1 = new JButton("清除");
		jb1.setBounds(590, 10, 80, 30);
		
		JLabel LH = new JLabel("Header：");
		LH.setBounds(10, 45, 120, 30);
		final JTextArea jt0 = new JTextArea();
		jt0.setBounds(10, 80, 600, 100);
		jt0.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
		JScrollPane js0 = new JScrollPane(jt0);
		// 分别设置水平和垂直滚动条自动出现
		js0.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		js0.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js0.setBounds(10, 80, 600, 100);
		
		JLabel L2 = new JLabel("Body：");
		L2.setBounds(10, 185, 120, 30);
		final JTextArea jt = new JTextArea();
		jt.setBounds(10, 220, 600, 200);
		jt.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
		JScrollPane js = new JScrollPane(jt);
		// 分别设置水平和垂直滚动条自动出现
		js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js.setBounds(10, 220, 600, 200);
		
		JLabel L3 = new JLabel("Response：");
		L3.setBounds(10, 425, 120, 30);
		final JTextArea jt1 = new JTextArea();
		jt1.setBounds(10, 460, 600, 200);
		jt1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
		jt1.setEditable(false);
		JScrollPane js1 = new JScrollPane(jt1);
		// 分别设置水平和垂直滚动条自动出现
		js1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js1.setBounds(10, 460, 600, 200);

		pane.add(L1);
		pane.add(t1);
		pane.add(c1);
		pane.add(jb);
		pane.add(jb1);
		pane.add(LH);
		pane.add(js0);
		pane.add(L2);
		pane.add(js);
		pane.add(L3);
		pane.add(js1);

		String testReq = "{\n" + "    'serviceId':'test',\n"
				+ "    'authCode':'5Yib5Lia5oWn5bq3fGN5X3VzZXIxfGFkbWluQDEyMw=='\n" + "}";
		jt.setText(testReq);

		jb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (StringUtils.isEmpty(t1.getText())) {
					JOptionPane.showMessageDialog(null, "url地址不能为空！");
					return;
				}
				if (StringUtils.isEmpty(jt.getText())) {
					JOptionPane.showMessageDialog(null, "请求数据不能为空！");
					return;
				}
				String url = t1.getText();
				String method = (String) c1.getSelectedItem();
				String headerStr = jt0.getText();
				String body = jt.getText();
				
				String responseText = "";
				
				JSONObject headerMap = null;
				
				if(!StringUtils.isEmpty(headerStr)) {
					headerMap = JSONObject.fromObject(headerStr);
				}
				
				if("Post".equals(method)) {
					responseText = HttpUtils.httpPost(url,headerMap,body);
				}
				
				else if("Get".equals(method)) {
					JSONObject paramMap = null;
					if(!StringUtils.isEmpty(body)) {
						paramMap = JSONObject.fromObject(body);
					}
					
					try {
						responseText = HttpUtils.httpGet(url, headerMap,paramMap);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else {}
				
				jt1.setText(responseText);
			}

		});

		jb1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jt.setText("");
				jt1.setText("");
			}

		});

		jf.setBounds(200, 15, 800, 700); // 设置窗口的属性 窗口位置以及窗口的大小
		jf.setVisible(true);// 设置窗口可见
		jf.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // 设置关闭方式 如果不设置的话 // 似乎关闭窗口之后不会退出程序
	}

	public static void main(String[] args) throws Exception {
		new HttpPostTestWindow("HttpPost请求");
	}

}
