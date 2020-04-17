package com.yoe.service;

import com.yoe.dao.GoodsDao;
import com.yoe.pojo.MiaoShaGoods;
import com.yoe.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 减库存
     * @param goodsVo
     */
    public boolean reduceStock(GoodsVo goodsVo) {

        MiaoShaGoods goods = new MiaoShaGoods();
        goods.setGoodsId(goodsVo.getId());
        int res = goodsDao.reduceStock(goods);
        return res > 0;
    }

}
