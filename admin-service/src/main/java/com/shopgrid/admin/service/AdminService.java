package com.shopgrid.admin.service;

import java.util.UUID;

public interface AdminService {

    void blockUser(UUID id, String token);

}
