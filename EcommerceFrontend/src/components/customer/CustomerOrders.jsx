import { useEffect, useState } from "react";
import axios from "axios";
import "./customer.css";

function CustomerOrders() {

  const [orders, setOrders] = useState([]);

  useEffect(() => {

    const token =
      localStorage.getItem("token");

    axios
      .get(
        "http://localhost:8080/api/orders/my-orders",
        {
            headers: {
                "Authorization": "Bearer " + token
            }
        }
      )
      .then((response) => {

        setOrders(response.data);

      })
      .catch((error) => {

        console.log(error);

      });

  }, []);

  return (

    <div className="orders-page">

      <h1 className="orders-title">
        My Orders
      </h1>

      {
        orders.length === 0 ? (

          <h2 className="empty-orders">
            No Orders Found
          </h2>

        ) : (

          orders.map((order) => (

            <div
              className="order-card"
              key={order.id}
            >

              <div className="order-header">

                <div>

                  <h3>
                    Order #{order.id}
                  </h3>

                  <p>
                    {new Date(
                      order.orderDate
                    ).toLocaleString()}
                  </p>

                </div>

                <div>

                  <h3 className="order-status">
                    {order.status}
                  </h3>

                  <h2 className="order-total">
                    ₹{order.totalPrice}
                  </h2>

                </div>

              </div>

              <div className="order-products">

                {order.orderItems.map((item) => (

                  <div
                    className="order-product"
                    key={item.id}
                  >

                    <img
                      src={
                        item.product.imageUrls?.[0]
                      }
                      alt={item.product.name}
                    />

                    <div>

                      <h3>
                        {item.product.name}
                      </h3>

                      <p>
                        Quantity:
                        {" "}
                        {item.quantity}
                      </p>

                      <p>
                        Price:
                        {" "}
                        ₹{item.price}
                      </p>

                      <p>
                        Subtotal:
                        {" "}
                        ₹{item.subtotal}
                      </p>

                    </div>

                  </div>

                ))}

              </div>

            </div>

          ))

        )
      }

    </div>

  );

}

export default CustomerOrders;