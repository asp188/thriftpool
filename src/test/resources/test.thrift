namespace java com.phatduckk.thriftpool.thriftpool.testing

/***********************************
 * A bogus thrift file for com.phatduckk.thriftpool.thriftpool.testing
 ***********************************/

/***********************************
 * zee structs
 ***********************************/
struct Address {
    1:string streetAddress,
    2:string city,
    3:string state,
    4:i32 zip
}

struct User {
    1:i32 userID,
    2:string username,
    3:optional Address address
}


/***********************************
 * Le service & its endpoints
 ***********************************/
service TestService {
    // get a User by id
    User getByID(1:i32 userID),

    // get all Users
    list<User> getAll(),

    // save a User
    bool put(1:User user)
}