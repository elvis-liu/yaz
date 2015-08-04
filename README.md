#Yet Another Zeratul [![Build Status](https://travis-ci.org/elvis-liu/yaz.svg?branch=master)](https://travis-ci.org/elvis-liu/yaz)

Yet Another Zeratul(YAZ) is a new version of [Zeratul](https://github.com/ThoughtWorksInc/zeratul).

YAZ provides a fundamental BaseDao, with convenient method for querying.

##Usage

Maven dependency:

```xml
<dependency>
    <groupId>com.exmertec.yaz</groupId>
    <artifactId>yaz</artifactId>
    <version>0.01</version>
    <type>jar</type>
    <scope>compile</scope>
</dependency>
```

To use it just:
    
```java
public class UserDao extends BaseDao<User> {
    public UserDao() {
        super(entityManager, User.class);
    }
    
    public List<User> findUserByName(String key) {
        return with(field("name").like(key), field("status").ne(DISABLED)).queryList();
    }
}
```
