import React from "react";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import UsersTable from "main/components/Users/UsersTable"

import { useUsers } from "main/utils/users";
const AdminUsersPage = () => {

    // const { data: users } = useUsers();
    const { data: users, isLoading, isError, error } = useUsers();
    if (isLoading) {
        history.push('/');
        return null; 
    }
    const abc = isLoading;
    console.log(abc,"ascs");
    console.log(isLoading);
    console.log(isError);
    console.log(error);

    return (
        <BasicLayout>
            <h2>Users</h2>
            <div>{isLoading}</div>
            <UsersTable users={users} />
        </BasicLayout>
    );
};

export default AdminUsersPage;
