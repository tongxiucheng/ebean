package com.avaje.tests.rawsql;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.tests.model.basic.OrderAggregate;
import com.avaje.tests.model.basic.OrderDetail;
import com.avaje.tests.model.basic.ResetBasicData;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class TestOrderReportTotal extends BaseTestCase {

  @Test
  public void test() {

    ResetBasicData.reset();

    RawSql rawSql = getRawSql();

    Query<OrderAggregate> query = Ebean.createQuery(OrderAggregate.class);

    List<OrderAggregate> list = query.setRawSql(rawSql).findList();
    assertNotNull(list);

    Query<OrderAggregate> q2 = Ebean.createQuery(OrderAggregate.class).setRawSql(rawSql);
    q2.where().gt("id", 1);
    q2.having().gt("totalItems", 1);

    List<OrderAggregate> l2 = q2.findList();
    assertNotNull(l2);

  }

  private RawSql getRawSql() {
    String sql =
        "select order_id, count(*) as totalItems, sum(order_qty*unit_price) as totalAmount \n" +
            "from o_order_detail \n" +
            "group by order_id";

    return RawSqlBuilder.parse(sql).columnMapping("order_id", "order.id").create();
  }

  @Test
  public void testOrderDetailCount() {

    ResetBasicData.reset();

    int detailsCount = Ebean.find(OrderDetail.class)
        .where()
          .gt("order.id", 2)
          .istartsWith("order.customer.name","rob")
        .findCount();

    assertThat(detailsCount).isGreaterThan(0);
  }
}
