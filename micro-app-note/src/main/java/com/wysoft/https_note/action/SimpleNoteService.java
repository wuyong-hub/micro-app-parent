package com.wysoft.https_note.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.wysoft.https_base.action.BaseService;
import com.wysoft.https_base.annotation.RemoteMethod;
import com.wysoft.https_base.util.JSONUtil;
import com.wysoft.https_note.dao.SimpleNoteDao;
import com.wysoft.https_note.model.SimpleNote;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 日记处理处理类.
 * @author Wuyong
 *
 */
@Service("simpleNoteService")
public class SimpleNoteService extends BaseService {
	@Autowired
	private SimpleNoteDao simpleNoteDao;
	
	@Autowired
	private JsonConfig jsonConfig;

	/**
	 * 保存日记.
	 * @param json 格式见{@link SimpleNote}
	 * @return {@link SimpleNote}
	 */
	@RemoteMethod
	public JSONObject save(JSONObject json) {
		SimpleNote note = (SimpleNote) JSONObject.toBean(json, SimpleNote.class);
		String dtStr = JSONUtil.getString(json, "recordTime");
		if (StringUtils.isNotEmpty(dtStr)) {
			try {
				note.setRecordTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dtStr));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			note.setRecordTime(new Date());
		}
		simpleNoteDao.save(note);
		return JSONUtil.getResult();
	}

	/**
	 * 日记查询.
	 * @param json 包含查询条件及分页信息,queryKey,{@link Pageable}}
	 * @return {@link Page}
	 */
	@RemoteMethod
	public JSONObject findByPage(JSONObject json) {
		JSONObject result = JSONUtil.getResult();
		
		Pageable pageable = JSONUtil.createPage(json);

		String queryKey = JSONUtil.getString(json, "queryKey");
		String uid = JSONUtil.getString(json, "uid");

		Page<SimpleNote> page = simpleNoteDao.findAll(new Specification<SimpleNote>() {

			@Override
			public Predicate toPredicate(Root<SimpleNote> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				Predicate resultP = null;
				if(StringUtils.isNotBlank(uid)) {
					resultP = cb.equal(root.get("uid").as(String.class),uid);
				}
				
				if (StringUtils.isNotBlank(queryKey)) { // 添加断言
					Predicate titleLike = cb.like(root.get("title").as(String.class), "%" + queryKey + "%");
					Predicate keywordsLike = cb.like(root.get("keywords").as(String.class), "%" + queryKey + "%");
					Predicate contentLike = cb.like(root.get("content").as(String.class), "%" + queryKey + "%");
					
					if(resultP != null) {
						resultP = cb.and(resultP,cb.or(titleLike,keywordsLike,contentLike));
					}else {
						resultP = cb.or(titleLike,keywordsLike,contentLike);
					}
				}
				
				return resultP;
			}

		}, pageable);

		result.put("data",JSONArray.fromObject(page.getContent(),jsonConfig));
		result.put("totalPages",page.getTotalPages());
		result.put("pageNumber",pageable.getPageNumber());
		result.put("pageSize",pageable.getPageSize());
		result.put("size",page.getSize());
		result.put("rows", page.getTotalElements());
		return result;
	}

	/**
	 * 通过主键查询.
	 * @param json id
	 * @return {@link SimpleNote}
	 */
	@RemoteMethod
	public JSONObject findById(JSONObject json) {
		Integer id = json.getInt("id");
		Optional<SimpleNote> option = simpleNoteDao.findById(id);
		SimpleNote note = null;
		if (option.isPresent()) {
			note = option.get();
		}

		if (note != null) {
			JSONObject result = JSONUtil.getResult();
			result.put("data", JSONObject.fromObject(note,jsonConfig));
			return result;
		} else {
			return JSONUtil.getErrMsg("记录不存在！");
		}
	}
	
	/**
	 * 删除日记.
	 * @param json id
	 * @return JSONObject
	 */
	@RemoteMethod
	public JSONObject remove(JSONObject json) {
		Integer id = json.getInt("id");
		simpleNoteDao.deleteById(id);
		return JSONUtil.getResult();
	}
}
