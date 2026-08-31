package com.quickkart.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.quickkart.app.models.CartItem;
import com.quickkart.app.models.Category;
import com.quickkart.app.models.Order;
import com.quickkart.app.models.OrderItem;
import com.quickkart.app.models.Product;
import com.quickkart.app.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Central local database for Quick Kart.
 * This mirrors the original PHP/MySQL schema (users, admin, categories,
 * products, orders, order_items) but stored on-device with SQLite, since a
 * compiled APK cannot run a PHP server. All install/seed logic that used to
 * live in install.php now happens automatically in onCreate().
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "quickkart.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_ADMIN = "admin";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_ORDER_ITEMS = "order_items";
    public static final String TABLE_CART = "cart";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "phone TEXT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "address TEXT," +
                "created_at INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_ADMIN + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "image TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cat_id INTEGER," +
                "name TEXT," +
                "description TEXT," +
                "price REAL," +
                "stock INTEGER," +
                "image TEXT," +
                "created_at INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "total_amount REAL," +
                "status TEXT," +
                "address TEXT," +
                "phone TEXT," +
                "created_at INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER," +
                "product_id INTEGER," +
                "product_name TEXT," +
                "image TEXT," +
                "quantity INTEGER," +
                "price REAL)");

        db.execSQL("CREATE TABLE " + TABLE_CART + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "product_id INTEGER," +
                "quantity INTEGER)");

        seedDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    // ---------------------------------------------------------------
    // SEED DATA  (equivalent of install.php default inserts)
    // ---------------------------------------------------------------
    private void seedDefaultData(SQLiteDatabase db) {
        ContentValues admin = new ContentValues();
        admin.put("username", "admin");
        admin.put("password", "admin123");
        db.insert(TABLE_ADMIN, null, admin);

        String[][] categories = {
                {"Mobiles", "cat_mobiles"},
                {"Fashion", "cat_fashion"},
                {"Electronics", "cat_electronics"},
                {"Home & Kitchen", "cat_home"},
                {"Beauty", "cat_beauty"},
                {"Grocery", "cat_grocery"},
                {"Footwear", "cat_footwear"},
                {"Toys", "cat_toys"}
        };
        for (String[] c : categories) {
            ContentValues cv = new ContentValues();
            cv.put("name", c[0]);
            cv.put("image", c[1]);
            db.insert(TABLE_CATEGORIES, null, cv);
        }

        Object[][] products = {
                {1, "Galaxy Smart Phone X12", "6.5 inch display, 128GB storage, 5000mAh battery", 14999.0, 25, "product_1"},
                {1, "Nova Buds Wireless Earphones", "Bluetooth 5.0, noise cancellation, 24hr playback", 1499.0, 60, "product_2"},
                {2, "Men's Casual Cotton Shirt", "Slim fit, breathable fabric, machine washable", 899.0, 100, "product_3"},
                {2, "Women's Ethnic Kurti Set", "Printed rayon kurti with matching palazzo", 1299.0, 80, "product_4"},
                {3, "4K Smart LED TV 43-inch", "Ultra HD, built-in apps, voice remote", 24999.0, 15, "product_5"},
                {3, "Bluetooth Speaker Boom Mini", "12W output, 10hr battery, water resistant", 1799.0, 45, "product_6"},
                {4, "Non-Stick Cookware Set (5pc)", "Induction friendly, soft-touch handles", 2199.0, 30, "product_7"},
                {4, "LED Table Lamp", "Touch control, 3 brightness modes", 799.0, 55, "product_8"},
                {5, "Herbal Face Wash Combo", "Pack of 2, for all skin types", 349.0, 120, "product_9"},
                {6, "Premium Basmati Rice 5kg", "Long grain, aged rice", 649.0, 90, "product_10"},
                {7, "Men's Running Sports Shoes", "Lightweight, cushioned sole", 1599.0, 70, "product_11"},
                {8, "Building Blocks Toy Set (200pc)", "Educational, non-toxic plastic", 999.0, 40, "product_12"}
        };
        for (Object[] p : products) {
            ContentValues cv = new ContentValues();
            cv.put("cat_id", (Integer) p[0]);
            cv.put("name", (String) p[1]);
            cv.put("description", (String) p[2]);
            cv.put("price", (Double) p[3]);
            cv.put("stock", (Integer) p[4]);
            cv.put("image", (String) p[5]);
            cv.put("created_at", System.currentTimeMillis());
            db.insert(TABLE_PRODUCTS, null, cv);
        }
    }

    // ---------------------------------------------------------------
    // USERS
    // ---------------------------------------------------------------
    public long registerUser(String name, String phone, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("phone", phone);
        cv.put("email", email);
        cv.put("password", password);
        cv.put("address", "");
        cv.put("created_at", System.currentTimeMillis());
        return db.insert(TABLE_USERS, null, cv);
    }

    public boolean isEmailTaken(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, new String[]{"id"}, "email=?", new String[]{email}, null, null, null);
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null, "email=? AND password=?",
                new String[]{email, password}, null, null, null);
        User user = null;
        if (c.moveToFirst()) {
            user = cursorToUser(c);
        }
        c.close();
        return user;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        User user = null;
        if (c.moveToFirst()) {
            user = cursorToUser(c);
        }
        c.close();
        return user;
    }

    public boolean updateUserProfile(int id, String name, String phone, String address) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("phone", phone);
        cv.put("address", address);
        return db.update(TABLE_USERS, cv, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean changeUserPassword(int id, String oldPass, String newPass) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_USERS, new String[]{"password"}, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        boolean ok = false;
        if (c.moveToFirst()) {
            String current = c.getString(0);
            if (current.equals(oldPass)) {
                ContentValues cv = new ContentValues();
                cv.put("password", newPass);
                db.update(TABLE_USERS, cv, "id=?", new String[]{String.valueOf(id)});
                ok = true;
            }
        }
        c.close();
        return ok;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null, null, null, null, null, "id DESC");
        while (c.moveToNext()) {
            list.add(cursorToUser(c));
        }
        c.close();
        return list;
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_USERS, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    private User cursorToUser(Cursor c) {
        User u = new User();
        u.id = c.getInt(c.getColumnIndexOrThrow("id"));
        u.name = c.getString(c.getColumnIndexOrThrow("name"));
        u.phone = c.getString(c.getColumnIndexOrThrow("phone"));
        u.email = c.getString(c.getColumnIndexOrThrow("email"));
        u.password = c.getString(c.getColumnIndexOrThrow("password"));
        u.address = c.getString(c.getColumnIndexOrThrow("address"));
        return u;
    }

    // ---------------------------------------------------------------
    // ADMIN
    // ---------------------------------------------------------------
    public boolean loginAdmin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ADMIN, null, "username=? AND password=?",
                new String[]{username, password}, null, null, null);
        boolean ok = c.getCount() > 0;
        c.close();
        return ok;
    }

    public boolean updateAdminCredentials(String username, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        if (newPassword != null && !newPassword.isEmpty()) {
            cv.put("password", newPassword);
        }
        return db.update(TABLE_ADMIN, cv, "id=1", null) > 0;
    }

    public String getAdminUsername() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ADMIN, new String[]{"username"}, "id=1", null, null, null, null);
        String name = "admin";
        if (c.moveToFirst()) name = c.getString(0);
        c.close();
        return name;
    }

    // ---------------------------------------------------------------
    // CATEGORIES
    // ---------------------------------------------------------------
    public long addCategory(String name, String imageKey) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("image", imageKey);
        return db.insert(TABLE_CATEGORIES, null, cv);
    }

    public boolean updateCategory(int id, String name, String imageKey) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        if (imageKey != null) cv.put("image", imageKey);
        return db.update(TABLE_CATEGORIES, cv, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteCategory(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_CATEGORIES, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_CATEGORIES, null, null, null, null, null, "id ASC");
        while (c.moveToNext()) {
            list.add(new Category(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("image"))
            ));
        }
        c.close();
        return list;
    }

    public Category getCategoryById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_CATEGORIES, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        Category cat = null;
        if (c.moveToFirst()) {
            cat = new Category(c.getInt(0), c.getString(1), c.getString(2));
        }
        c.close();
        return cat;
    }

    // ---------------------------------------------------------------
    // PRODUCTS
    // ---------------------------------------------------------------
    public long addProduct(int catId, String name, String desc, double price, int stock, String imageKey) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cat_id", catId);
        cv.put("name", name);
        cv.put("description", desc);
        cv.put("price", price);
        cv.put("stock", stock);
        cv.put("image", imageKey);
        cv.put("created_at", System.currentTimeMillis());
        return db.insert(TABLE_PRODUCTS, null, cv);
    }

    public boolean updateProduct(int id, int catId, String name, String desc, double price, int stock, String imageKey) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("cat_id", catId);
        cv.put("name", name);
        cv.put("description", desc);
        cv.put("price", price);
        cv.put("stock", stock);
        if (imageKey != null) cv.put("image", imageKey);
        return db.update(TABLE_PRODUCTS, cv, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<Product> getAllProducts() {
        return queryProducts(null, null, null);
    }

    public List<Product> getProductsByCategory(int catId) {
        return queryProducts("cat_id=?", new String[]{String.valueOf(catId)}, null);
    }

    public List<Product> searchProducts(String keyword, String sortBy) {
        return queryProducts("name LIKE ?", new String[]{"%" + keyword + "%"}, sortBy);
    }

    public List<Product> getProductsSorted(String sortBy) {
        return queryProducts(null, null, sortBy);
    }

    private List<Product> queryProducts(String where, String[] args, String sortBy) {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String orderBy = "created_at DESC";
        if ("price_low".equals(sortBy)) orderBy = "price ASC";
        else if ("price_high".equals(sortBy)) orderBy = "price DESC";
        else if ("new".equals(sortBy)) orderBy = "created_at DESC";

        Cursor c = db.query(TABLE_PRODUCTS, null, where, args, null, null, orderBy);
        while (c.moveToNext()) {
            list.add(cursorToProduct(c));
        }
        c.close();
        return list;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PRODUCTS, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        Product p = null;
        if (c.moveToFirst()) {
            p = cursorToProduct(c);
        }
        c.close();
        return p;
    }

    public List<Product> getRelatedProducts(int catId, int excludeId) {
        return queryProducts("cat_id=? AND id!=?", new String[]{String.valueOf(catId), String.valueOf(excludeId)}, null);
    }

    private Product cursorToProduct(Cursor c) {
        Product p = new Product();
        p.id = c.getInt(c.getColumnIndexOrThrow("id"));
        p.categoryId = c.getInt(c.getColumnIndexOrThrow("cat_id"));
        p.name = c.getString(c.getColumnIndexOrThrow("name"));
        p.description = c.getString(c.getColumnIndexOrThrow("description"));
        p.price = c.getDouble(c.getColumnIndexOrThrow("price"));
        p.stock = c.getInt(c.getColumnIndexOrThrow("stock"));
        p.imageKey = c.getString(c.getColumnIndexOrThrow("image"));
        p.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
        Category cat = getCategoryById(p.categoryId);
        p.categoryName = cat != null ? cat.name : "";
        return p;
    }

    // ---------------------------------------------------------------
    // CART
    // ---------------------------------------------------------------
    public void addToCart(int userId, int productId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLE_CART, new String[]{"id", "quantity"}, "user_id=? AND product_id=?",
                new String[]{String.valueOf(userId), String.valueOf(productId)}, null, null, null);
        if (c.moveToFirst()) {
            int cartId = c.getInt(0);
            int existingQty = c.getInt(1);
            ContentValues cv = new ContentValues();
            cv.put("quantity", existingQty + quantity);
            db.update(TABLE_CART, cv, "id=?", new String[]{String.valueOf(cartId)});
        } else {
            ContentValues cv = new ContentValues();
            cv.put("user_id", userId);
            cv.put("product_id", productId);
            cv.put("quantity", quantity);
            db.insert(TABLE_CART, null, cv);
        }
        c.close();
    }

    public void updateCartQuantity(int cartId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("quantity", quantity);
        db.update(TABLE_CART, cv, "id=?", new String[]{String.valueOf(cartId)});
    }

    public void removeFromCart(int cartId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CART, "id=?", new String[]{String.valueOf(cartId)});
    }

    public void clearCart(int userId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CART, "user_id=?", new String[]{String.valueOf(userId)});
    }

    public List<CartItem> getCartItems(int userId) {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT c.id, c.product_id, c.quantity, p.name, p.image, p.price, p.stock " +
                "FROM " + TABLE_CART + " c JOIN " + TABLE_PRODUCTS + " p ON c.product_id = p.id " +
                "WHERE c.user_id=?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            CartItem item = new CartItem();
            item.cartId = c.getInt(0);
            item.productId = c.getInt(1);
            item.quantity = c.getInt(2);
            item.name = c.getString(3);
            item.imageKey = c.getString(4);
            item.price = c.getDouble(5);
            item.stock = c.getInt(6);
            list.add(item);
        }
        c.close();
        return list;
    }

    public int getCartCount(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(quantity) FROM " + TABLE_CART + " WHERE user_id=?",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (c.moveToFirst() && !c.isNull(0)) count = c.getInt(0);
        c.close();
        return count;
    }

    // ---------------------------------------------------------------
    // ORDERS
    // ---------------------------------------------------------------
    public long placeOrder(int userId, List<CartItem> items, double total, String address, String phone) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long orderId = -1;
        try {
            ContentValues cv = new ContentValues();
            cv.put("user_id", userId);
            cv.put("total_amount", total);
            cv.put("status", "Placed");
            cv.put("address", address);
            cv.put("phone", phone);
            cv.put("created_at", System.currentTimeMillis());
            orderId = db.insert(TABLE_ORDERS, null, cv);

            for (CartItem item : items) {
                ContentValues itemCv = new ContentValues();
                itemCv.put("order_id", orderId);
                itemCv.put("product_id", item.productId);
                itemCv.put("product_name", item.name);
                itemCv.put("image", item.imageKey);
                itemCv.put("quantity", item.quantity);
                itemCv.put("price", item.price);
                db.insert(TABLE_ORDER_ITEMS, null, itemCv);

                ContentValues stockCv = new ContentValues();
                stockCv.put("stock", Math.max(0, item.stock - item.quantity));
                db.update(TABLE_PRODUCTS, stockCv, "id=?", new String[]{String.valueOf(item.productId)});
            }

            db.delete(TABLE_CART, "user_id=?", new String[]{String.valueOf(userId)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return orderId;
    }

    public List<Order> getOrdersByUser(int userId) {
        return queryOrders("user_id=?", new String[]{String.valueOf(userId)});
    }

    public List<Order> getAllOrders() {
        return queryOrders(null, null);
    }

    public Order getOrderById(int orderId) {
        List<Order> list = queryOrders("id=?", new String[]{String.valueOf(orderId)});
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Order> queryOrders(String where, String[] args) {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT o.id, o.user_id, o.total_amount, o.status, o.address, o.phone, o.created_at, u.name " +
                "FROM " + TABLE_ORDERS + " o LEFT JOIN " + TABLE_USERS + " u ON o.user_id = u.id";
        if (where != null) sql += " WHERE " + where;
        sql += " ORDER BY o.id DESC";
        Cursor c = db.rawQuery(sql, args);
        while (c.moveToNext()) {
            Order o = new Order();
            o.id = c.getInt(0);
            o.userId = c.getInt(1);
            o.totalAmount = c.getDouble(2);
            o.status = c.getString(3);
            o.address = c.getString(4);
            o.phone = c.getString(5);
            o.createdAt = c.getLong(6);
            o.userName = c.getString(7);
            o.items = getOrderItems(o.id);
            list.add(o);
        }
        c.close();
        return list;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ORDER_ITEMS, null, "order_id=?", new String[]{String.valueOf(orderId)}, null, null, "id ASC");
        while (c.moveToNext()) {
            OrderItem item = new OrderItem();
            item.id = c.getInt(c.getColumnIndexOrThrow("id"));
            item.orderId = c.getInt(c.getColumnIndexOrThrow("order_id"));
            item.productId = c.getInt(c.getColumnIndexOrThrow("product_id"));
            item.productName = c.getString(c.getColumnIndexOrThrow("product_name"));
            item.imageKey = c.getString(c.getColumnIndexOrThrow("image"));
            item.quantity = c.getInt(c.getColumnIndexOrThrow("quantity"));
            item.price = c.getDouble(c.getColumnIndexOrThrow("price"));
            list.add(item);
        }
        c.close();
        return list;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        return db.update(TABLE_ORDERS, cv, "id=?", new String[]{String.valueOf(orderId)}) > 0;
    }

    // ---------------------------------------------------------------
    // ADMIN DASHBOARD STATS
    // ---------------------------------------------------------------
    public int countRows(String table) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + table, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public double getTotalRevenue() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(total_amount) FROM " + TABLE_ORDERS + " WHERE status != 'Cancelled'", null);
        double total = 0;
        if (c.moveToFirst() && !c.isNull(0)) total = c.getDouble(0);
        c.close();
        return total;
    }

    public int countOrdersByStatus(String status) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ORDERS + " WHERE status=?", new String[]{status});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }
}
