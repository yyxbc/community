package com.xbc.community.admin.controller;

import com.xbc.community.admin.dto.PageInfo;
import com.xbc.community.dto.ResultDTO;
import com.xbc.community.productSeckill.entity.Seckill;
import com.xbc.community.productSeckill.redis.RedisUtil;
import com.xbc.community.productSeckill.service.ISeckillService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/main/product")
public class ProductController {

    @Autowired
    ISeckillService seckillService;

    @Autowired
    RedisUtil redisUtil;

    @ModelAttribute
    public Seckill getSeckill(Integer id) {
        Seckill seckill = null;

        // id 不为空，则从数据库获取
        if (id != null&&!id.equals("")) {
            seckill = seckillService.findById(Long.parseLong(Integer.toString(id)));
        } else {
            seckill = new Seckill();
        }

        return seckill;
    }

    /**
     * 跳转到用户列表页
     *
     * @return
     */

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(Model model) {
        List<Seckill> seckills = seckillService.findAll();
        ResultDTO resultDTO =ResultDTO.okOf();
        resultDTO.setData(seckills);
        model.addAttribute("ResultDTO", resultDTO);
        return "admin/views/product_list";
    }

    /**
     * 跳转用户表单页
     *
     * @return
     */

    @RequestMapping(value = "form", method = RequestMethod.GET)
    public String form() {
        return "admin/views/product_form";
    }

    /**
     * 保存用户信息
     *
     * @param tbUser
     * @return
     */

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(Seckill seckill, Model model, RedirectAttributes redirectAttributes) {
        ResultDTO resultDTO = new ResultDTO();
        int num = 0;
        if (StringUtils.isNotBlank(Long.toString(seckill.getSeckillId()))) {
            num = seckillService.updateall(seckill);
        } else {
            num  = seckillService.insert(seckill);
        }
        // 保存成功
        if (num == 1) {
            redirectAttributes.addFlashAttribute("resultDTO", ResultDTO.okOf());
            return "redirect:/main/product/list";
        }
        // 保存失败
        else {
            model.addAttribute("resultDTO", resultDTO.errorOf());
            return "admin/views/product_form";
        }
    }

    /**
     * 删除用户信息
     *
     * @param ids
     * @return
     */

    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public ResultDTO delete(String ids) {
        ResultDTO resultDTO = null;
        if (StringUtils.isNotBlank(ids)) {
            String[] idArray = ids.split(",");
            int count = seckillService.delteByid(idArray);
            if(count==idArray.length){
                resultDTO = ResultDTO.okOf();
                resultDTO.setMessage("删除成功");
            }else{
                resultDTO = ResultDTO.errorOf();
                resultDTO.setMessage("删除失败");
            }
        } else {
            resultDTO = ResultDTO.errorOf();
            resultDTO.setMessage("删除失败");
        }

        return resultDTO;
    }

    /**
     * 显示用户详情
     *
     * @return
     */

    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            model.addAttribute("seckill", seckillService.findById(Long.parseLong(Integer.toString(id))));
        }
        return "admin/views/product_detail";
    }

    @ResponseBody
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public PageInfo<Seckill> page(HttpServletRequest request) {
        String strDraw = request.getParameter("draw");
        String strStart = request.getParameter("start");
        String strLength = request.getParameter("length");

        int draw = strDraw == null ? 0 : Integer.parseInt(strDraw);
        int start = strStart == null ? 0 : Integer.parseInt(strStart);
        int length = strLength == null ? 10 : Integer.parseInt(strLength);
        start =start/10+1;
        // 封装 Datatables 需要的结果
        int count = seckillService.findAll().size();
        PageInfo<Seckill> pageInfo = new PageInfo<>();
        System.out.println(seckillService.findAll().toString());
        pageInfo.setDraw(draw);
        pageInfo.setRecordsTotal(count);
        pageInfo.setRecordsFiltered(count);
        pageInfo.setData(seckillService.findall(start, length).getList());
        return pageInfo;
    }

}
