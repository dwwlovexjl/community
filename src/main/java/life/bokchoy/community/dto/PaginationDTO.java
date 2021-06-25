package life.bokchoy.community.dto;

import life.bokchoy.community.model.Question;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bokchoy
 * @description: 定义page
 * @date 2021年06月03日 14:17
 */
@Data
public class PaginationDTO<T> {
    private List<T> data;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private Integer totalPage;
    private List<Integer> pages=new ArrayList<>();


    public void setPagination(Integer totalPage, Integer page) {
        /*
         * 
         * @author bokchoy
         * 验证逻辑验证切换页面
         * @date 2021/6/3 16:49
         * @param totalCount
         * @param page
         * @param size 
         */
        this.page=page;
        this.totalPage=totalPage;
        //设置可以跳转的页面，当前页面前后各三个，若越界则不添加
        pages.add(page);
        for (int i = 1; i < 4; i++) {
            if (page-i>0) {
                pages.add(0,page-i);
            }
            if (page+i<=totalPage) {
                pages.add(page+i);
            }
        }

        //是否展示上一页
        if (page == 1) {
            showPrevious = false;
        } else {
            showPrevious = true;
        }
        //是否展示下一页
        if (page==totalPage) {
            showNext=false;
        }else {
            showNext=true;
        }
        
        //是否展示第一页
        if (pages.contains(1)) {
            showFirstPage=false;
        }else {
            showFirstPage=true;
        }
        
        //是否展示最后一页
        if (pages.contains(totalPage)) {
            showEndPage=false;
        }else {
            showEndPage=true;
        }
    }
}
