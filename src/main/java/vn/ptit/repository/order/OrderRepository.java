package vn.ptit.repository.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.ptit.model.Cash;
import vn.ptit.model.DigitalWallet;
import vn.ptit.model.Order;
import vn.ptit.model.QueryFilter;
import vn.ptit.repository.payment.IPaymentRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository implements IOrderRepository {
    private final OrderJpa orderJpa;
    private final IPaymentRepository paymentRepository;

    public OrderRepository(OrderJpa orderJpa, IPaymentRepository paymentRepository) {
        this.orderJpa = orderJpa;

        this.paymentRepository = paymentRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = OrderEntity.fromDomain(order);
        orderEntity = orderJpa.save(orderEntity);
        return orderEntity.toDomain();
    }

    @Override
    public List<Order> findByUser(String username, QueryFilter filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getLimit(),
                filter.getSort().equals("asc") ? Sort.by("updatedAt").ascending() : Sort.by("updatedAt").descending());
        List<OrderEntity> orderEntities = orderJpa.findByUser_Username(username, pageable);
        List<Order> result = new ArrayList<>();
        orderEntities.forEach(
                orderEntity -> {
                    Cash cash = paymentRepository.getCashById(orderEntity.getPayment().getId());
                    DigitalWallet digitalWallet = paymentRepository.getDigitalWalletById(orderEntity.getPayment().getId());
                    if (cash != null) {
                        Order order = orderEntity.toDomain();
                        order.setPayment(cash);
                        result.add(order);
                    } else {
                        Order order = orderEntity.toDomain();
                        order.setPayment(digitalWallet);
                        result.add(order);
                    }
                }
        );
        return result;
    }

    @Override
    public List<Order> findAll(QueryFilter filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getLimit(),
                filter.getSort().equals("asc") ? Sort.by("updatedAt").ascending() : Sort.by("updatedAt").descending());
        Page<OrderEntity> orderEntities = orderJpa.findAll(pageable);
        List<Order> result = new ArrayList<>();
        orderEntities.forEach(
                orderEntity -> {
                    Cash cash = paymentRepository.getCashById(orderEntity.getPayment().getId());
                    DigitalWallet digitalWallet = paymentRepository.getDigitalWalletById(orderEntity.getPayment().getId());
                    if (cash != null) {
                        Order order = orderEntity.toDomain();
                        order.setPayment(cash);
                        result.add(order);
                    } else {
                        Order order = orderEntity.toDomain();
                        order.setPayment(digitalWallet);
                        result.add(order);
                    }
                }
        );
        return result;
    }

    @Override
    public Order findById(long id) {
        Optional<OrderEntity> opt = orderJpa.findById(id);
        if(opt.isPresent()) {
            OrderEntity orderEntity = opt.get();
            Order order = orderEntity.toDomain();
            Cash cash = paymentRepository.getCashById(orderEntity.getPayment().getId());
            DigitalWallet digitalWallet = paymentRepository.getDigitalWalletById(orderEntity.getPayment().getId());
            if (cash != null) {
                order.setPayment(cash);
            } else {
                order.setPayment(digitalWallet);
            }
            return order;
        }
        return null;
    }

    @Transactional
    @Override
    public void updateOrderStatus(int status, long id) {
        orderJpa.updateOrderStatus(status, new Timestamp(System.currentTimeMillis()), id);
    }
}
