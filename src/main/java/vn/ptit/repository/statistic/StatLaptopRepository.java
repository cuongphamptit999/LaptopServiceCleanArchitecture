package vn.ptit.repository.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import vn.ptit.model.ImageLaptop;
import vn.ptit.model.Laptop;
import vn.ptit.model.LaptopStat;
import vn.ptit.model.QueryFilter;
import vn.ptit.repository.laptop.ImageJpa;
import vn.ptit.repository.laptop.ImageLaptopEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StatLaptopRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ImageJpa imageJpa;

    public List<LaptopStat> laptopWithTotalSold(QueryFilter filter) {
        String sql = "SELECT laptops.*, A.SoLuong FROM laptops, " +
                "(SELECT sum(quantity) AS SoLuong, laptop_id FROM line_items, orders " +
                "WHERE status = 2 AND line_items.cart_id = orders.cart_id " +
                "GROUP BY laptop_id) AS A " +
                "WHERE laptops.id = A.laptop_id AND is_delete = FALSE";

        if (filter.getSort().equals("asc")) {
            sql += " order by A.SoLuong asc";
        } else sql += " order by A.SoLuong desc";

        Query query = entityManager.createNativeQuery(sql);

        query.setFirstResult(filter.getPage() * filter.getLimit());
        query.setMaxResults(filter.getLimit());
        List<Object[]> records = query.getResultList();

        List<LaptopStat> laptopStats = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            LaptopStat laptopStat = new LaptopStat();
            laptopStat.setId(Long.parseLong(records.get(i)[0].toString()));
            laptopStat.setCategory(Laptop.Category.getById(Integer.parseInt(records.get(i)[3].toString())));
            laptopStat.setCpu(records.get(i)[4].toString());
            laptopStat.setDiscount(Double.parseDouble(records.get(i)[5].toString()));
            laptopStat.setHardDrive(records.get(i)[6].toString());
            laptopStat.setName(records.get(i)[8].toString());
            laptopStat.setPrice(Double.parseDouble(records.get(i)[9].toString()));
            laptopStat.setRam(records.get(i)[10].toString());
            laptopStat.setScreen(Double.parseDouble(records.get(i)[11].toString()));
            laptopStat.setSpecifications(records.get(i)[12].toString());
            laptopStat.setVga(records.get(i)[13].toString());
            laptopStat.setVideo(records.get(i)[14].toString());
            laptopStat.setTotalSold(Integer.parseInt(records.get(i)[16].toString()));

            List<ImageLaptop> imageLaptops = imageJpa.findByLaptop_Id(laptopStat.getId()).stream().map(ImageLaptopEntity::toDomain).collect(Collectors.toList());
            laptopStat.setImages(imageLaptops);
            laptopStats.add(laptopStat);
        }

        return laptopStats;
    }
}
