package com.wysoft.https_auth.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.iflytek.cloud.speech.ResourceUtil;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechEvent;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUtility;
import com.iflytek.cloud.speech.SynthesizerListener;
import com.wysoft.https_base.action.BaseService;
import com.wysoft.https_base.annotation.RemoteMethod;
import com.wysoft.https_base.util.JSONUtil;

import net.sf.json.JSONObject;

/**
 * 基于讯飞的人工智能服务. 可能囊括的内容有：语音识别、语音合成、人脸识别、文字识别、图像识别、自然语言处理.
 * 
 * @author Wuyong at 2020.12.16
 *
 */
@Service("aiService")
public class AIService extends BaseService {
	// 语音合成对象
	private SpeechSynthesizer mTts;
	private Map<String, String[]> mVoiceMap = new LinkedHashMap<String, String[]>();
	private Map<String, String> mParamMap = new HashMap<String, String>();

	private static class DefaultValue {
		public static final String ENG_TYPE = SpeechConstant.TYPE_CLOUD;
		public static final String VOICE = "小燕";
		public static final String BG_SOUND = "0";
		public static final String SPEED = "50";
		public static final String PITCH = "50";
		public static final String VOLUME = "50";
	}

	/**
	 * 在线语音合成.
	 * @param json
	 * @return
	 */
	@RemoteMethod
	public JSONObject speechSynthesis(JSONObject json) {
		String content = JSONUtil.getString(json, "content");
		if(StringUtils.isEmpty(content)) {
			return JSONUtil.getErrMsg("content为空！");
		}
		init();
		if (SpeechSynthesizer.getSynthesizer() == null)
			SpeechSynthesizer.createSynthesizer();
		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer();

		initParamMap();
		initVoiceMap();
		setting();
		// 合成文本为content的句子，设置监听器为mSynListener
		mTts.startSpeaking(content, mSynListener);
		
		return JSONUtil.getResult();
	}

	private void init() {
		// 初始化
		StringBuffer param = new StringBuffer();
		param.append("appid=5fd8721b");
//			param.append( ","+SpeechConstant.LIB_NAME_32+"=myMscName" );
		SpeechUtility.createUtility(param.toString());
	}

	private SynthesizerListener mSynListener = new SynthesizerListener() {

		@Override
		public void onSpeakBegin() {
		}

		@Override
		public void onBufferProgress(int progress, int beginPos, int endPos, String info) {
			System.out.println(
					"--onBufferProgress--progress:" + progress + ",beginPos:" + beginPos + ",endPos:" + endPos);
		}

		@Override
		public void onSpeakPaused() {

		}

		@Override
		public void onSpeakResumed() {

		}

		@Override
		public void onSpeakProgress(int progress, int beginPos, int endPos) {
			System.out.println(
					"onSpeakProgress enter progress:" + progress + ",beginPos:" + beginPos + ",endPos:" + endPos);

			// updateText( mText.substring( beginPos, endPos+1 ) );

			System.out.println("onSpeakProgress leave");
		}

		@Override
		public void onCompleted(SpeechError error) {
			System.out.println("onCompleted enter");

			// String text = mText;
			if (null != error) {
				System.out.println("onCompleted Code：" + error.getErrorCode());
				// text = error.getErrorDescription(true);
			}

			// updateText( text );

			System.out.println("onCompleted leave");
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, int arg3, Object obj1, Object obj2) {
			if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
				System.out.println("onEvent: type=" + eventType + ", arg1=" + arg1 + ", arg2=" + arg2 + ", arg3=" + arg3
						+ ", obj2=" + (String) obj2);
				ArrayList<?> bufs = null;
				if (obj1 instanceof ArrayList<?>) {
					bufs = (ArrayList<?>) obj1;
				} else {
					System.out.println("onEvent error obj1 is not ArrayList !");
				} // end of if-else instance of ArrayList

				if (null != bufs) {
					for (final Object obj : bufs) {
						if (obj instanceof byte[]) {
							final byte[] buf = (byte[]) obj;
							System.out.println("onEvent buf length: " + buf.length);
						} else {
							System.out.println("onEvent error element is not byte[] !");
						}
					} // end of for
				} // end of if bufs not null
			} // end of if tts buffer event
				// 以下代码用于调试，如果出现问题可以将sid提供给讯飞开发者，用于问题定位排查
			/*
			 * else if(SpeechEvent.EVENT_SESSION_ID == eventType) {
			 * System.out.println("sid=="+(String)obj2); }
			 */
		}
	};

	void setting() {
		final String engType = this.mParamMap.get(SpeechConstant.ENGINE_TYPE);
		String voiceName = null;

		for (Entry<String, String> entry : this.mParamMap.entrySet()) {
			String value = entry.getValue();
			if (SpeechConstant.VOICE_NAME.equals(entry.getKey())) {
				String[] names = this.mVoiceMap.get(entry.getValue());
				voiceName = value = SpeechConstant.TYPE_CLOUD.equals(engType) ? names[0] : names[1];
			}

			mTts.setParameter(entry.getKey(), value);
		}

		// 本地合成时设置资源，并启动引擎
		if (SpeechConstant.TYPE_LOCAL.equals(engType)) {
			// 启动合成引擎
			mTts.setParameter(ResourceUtil.ENGINE_START, SpeechConstant.ENG_TTS);
			// 设置资源路径
			String curPath = System.getProperty("user.dir");
			System.out.println("Current path=" + curPath);
			String resPath = ResourceUtil.generateResourcePath(curPath + "/tts/common.jet") + ";"
					+ ResourceUtil.generateResourcePath(curPath + "/tts/" + voiceName + ".jet");
			System.out.println("resPath=" + resPath);
			mTts.setParameter(ResourceUtil.TTS_RES_PATH, resPath);
		} // end of if is TYPE_LOCAL

		// 启用合成音频流事件，不需要时，不用设置此参数
		mTts.setParameter(SpeechConstant.TTS_BUFFER_EVENT, "1");
	}// end of function setting

	private void initParamMap() {
		this.mParamMap.put(SpeechConstant.ENGINE_TYPE, DefaultValue.ENG_TYPE);
		this.mParamMap.put(SpeechConstant.VOICE_NAME, DefaultValue.VOICE);
		this.mParamMap.put(SpeechConstant.BACKGROUND_SOUND, DefaultValue.BG_SOUND);
		this.mParamMap.put(SpeechConstant.SPEED, DefaultValue.SPEED);
		this.mParamMap.put(SpeechConstant.PITCH, DefaultValue.PITCH);
		this.mParamMap.put(SpeechConstant.VOLUME, DefaultValue.VOLUME);
		this.mParamMap.put(SpeechConstant.TTS_AUDIO_PATH, null);
	}

	// 初始化发音人镜像表，云端对应的本地
	// 为了查找本地资源方便，请把资源文件置为发音人参数+.jet，如“xiaoyan.jet”
	void initVoiceMap() {
		mVoiceMap.clear();
		String[] names = null;

		names = new String[2];
		names[0] = names[1] = "xiaoyan";
		this.mVoiceMap.put("小燕", names); // 小燕

		names = new String[2];
		names[0] = names[1] = "xiaoyu";
		this.mVoiceMap.put("小宇", names); // 小宇

		names = new String[2];
		names[0] = "vixf";
		names[1] = "xiaofeng";
		this.mVoiceMap.put("小峰", names); // 小峰

		names = new String[2];
		names[0] = "vixm";
		names[1] = "xiaomei";
		this.mVoiceMap.put("小梅", names); // 小梅

		names = new String[2];
		names[0] = "vixr";
		names[1] = "xiaorong";
		this.mVoiceMap.put("小蓉", names); // 小蓉

		names = new String[2];
		names[0] = names[1] = "catherine";
		this.mVoiceMap.put("凯瑟琳", names); // 凯瑟琳
	}
}
