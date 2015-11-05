#Yet Another Zeratul [![Build Status](https://travis-ci.org/elvis-liu/yaz.svg?branch=master)](https://travis-ci.org/elvis-liu/yaz)

Yet Another Zeratul(YAZ) is a new version of [Zeratul](https://github.com/ThoughtWorksInc/zeratul).

YAZ provides a fundamental BaseDao, with convenient method for querying.

##Usage

Maven dependency:

```xml
<dependency>
    <groupId>com.exmertec.yaz</groupId>
    <artifactId>yaz</artifactId>
    <version>0.06</version>
    <type>jar</type>
    <scope>compile</scope>
</dependency>
```

To use it just:
    
```java
public class UserDao extends BaseDao<User> {
    public UserDao() {
        super(User.class);
    }
    
    @PersistenceContext
    public void injectEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
    
    public List<User> findUserByName(String key) {
        return where(field("name").like(key), field("status").ne(DISABLED)).queryList();
    }
}
```

To make it easier, it's recommended to add a BaseDaoWrapper in your project:

```java
public abstract class BaseDaoWrapper<T> extends BaseDao<T> {
    protected BaseDaoWrapper(Class<T> prototype) {
        super(prototype);
    }

    @PersistenceContext
    public void injectEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}

public class UserDao extends BaseDaoWrapper<User> {
    public UserDao() {
        super(User.class);
    }
    
    public List<User> findUserByName(String key) {
        return where(field("name").like(key), field("status").ne(DISABLED)).queryList();
    }
}
```

Or if you're trying to use DDD repository pattern:

```java
public class UserDao extends BaseDaoWrapper<User> {
    public UserDao() {
        super(User.class);
    }
}

public class UserRepository extends BaseRepository<User> {
    private UserDao userDao;

    public User find(String userId) {
        return userDao.idEquals(userId).querySingle();
    }
}
```

Please refer to the test source code, which shows how to use different query methods provided by YAZ.
