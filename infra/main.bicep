param virtualMachines_vm_veterinaria_dev_name string = 'vm-veterinaria-dev'
param storageAccounts_stveterinariadev2026_name string = 'stveterinariadev2026'
param networkInterfaces_vm_veterinaria_dev400_name string = 'vm-veterinaria-dev400'
param publicIPAddresses_vm_veterinaria_dev_ip_name string = 'vm-veterinaria-dev-ip'
param virtualNetworks_vm_veterinaria_dev_vnet_name string = 'vm-veterinaria-dev-vnet'
param flexibleServers_mysql_veterinaria_dev_name string = 'mysql-veterinaria-dev'
param networkSecurityGroups_vm_veterinaria_dev_nsg_name string = 'vm-veterinaria-dev-nsg'

resource flexibleServers_mysql_veterinaria_dev_name_resource 'Microsoft.DBforMySQL/flexibleServers@2025-06-01-preview' = {
  name: flexibleServers_mysql_veterinaria_dev_name
  location: 'Chile Central'
  sku: {
    name: 'Standard_B1ms'
    tier: 'Burstable'
  }
  properties: {
    administratorLogin: 'sqldiegoupn'
    storage: {
      storageSizeGB: 20
      iops: 360
      autoGrow: 'Enabled'
      autoIoScaling: 'Enabled'
      logOnDisk: 'Disabled'
      storageRedundancy: 'LocalRedundancy'
    }
    version: '8.0.21'
    availabilityZone: '3'
    maintenanceWindow: {
      batchOfMaintenance: 'Default'
      customWindow: 'Disabled'
      dayOfWeek: 0
      startHour: 0
      startMinute: 0
    }
    replicationRole: 'None'
    network: {
      publicNetworkAccess: 'Enabled'
    }
    backup: {
      backupRetentionDays: 7
      backupIntervalHours: 24
      geoRedundantBackup: 'Disabled'
    }
    highAvailability: {
      mode: 'Disabled'
      replicationMode: 'BinaryLog'
    }
    maintenancePolicy: {
      patchStrategy: 'VirtualCanary'
    }
    databasePort: 3306
  }
}

resource networkSecurityGroups_vm_veterinaria_dev_nsg_name_resource 'Microsoft.Network/networkSecurityGroups@2025-05-01' = {
  name: networkSecurityGroups_vm_veterinaria_dev_nsg_name
  location: 'chilecentral'
  properties: {
    securityRules: [
      {
        name: 'SSH'
        id: networkSecurityGroups_vm_veterinaria_dev_nsg_name_SSH.id
        properties: {
          protocol: 'TCP'
          sourcePortRange: '*'
          destinationPortRange: '22'
          sourceAddressPrefix: '*'
          destinationAddressPrefix: '*'
          access: 'Allow'
          priority: 300
          direction: 'Inbound'
          sourcePortRanges: []
          destinationPortRanges: []
          sourceAddressPrefixes: []
          destinationAddressPrefixes: []
        }
      }
      {
        name: 'Allow-Tomcat-8080'
        id: networkSecurityGroups_vm_veterinaria_dev_nsg_name_Allow_Tomcat_8080.id
        properties: {
          protocol: 'TCP'
          sourcePortRange: '*'
          destinationPortRange: '8080'
          sourceAddressPrefix: '*'
          destinationAddressPrefix: '*'
          access: 'Allow'
          priority: 1010
          direction: 'Inbound'
          sourcePortRanges: []
          destinationPortRanges: []
          sourceAddressPrefixes: []
          destinationAddressPrefixes: []
        }
      }
    ]
  }
}

resource publicIPAddresses_vm_veterinaria_dev_ip_name_resource 'Microsoft.Network/publicIPAddresses@2025-05-01' = {
  name: publicIPAddresses_vm_veterinaria_dev_ip_name
  location: 'chilecentral'
  sku: {
    name: 'Standard'
    tier: 'Regional'
  }
  properties: {
    ipAddress: '57.156.64.113'
    publicIPAddressVersion: 'IPv4'
    publicIPAllocationMethod: 'Static'
    idleTimeoutInMinutes: 4
    ipTags: []
    ddosSettings: {
      protectionMode: 'VirtualNetworkInherited'
    }
  }
}

resource virtualNetworks_vm_veterinaria_dev_vnet_name_resource 'Microsoft.Network/virtualNetworks@2025-05-01' = {
  name: virtualNetworks_vm_veterinaria_dev_vnet_name
  location: 'chilecentral'
  properties: {
    addressSpace: {
      addressPrefixes: [
        '10.0.0.0/16'
      ]
    }
    privateEndpointVNetPolicies: 'Disabled'
    subnets: [
      {
        name: 'subnet-app'
        id: virtualNetworks_vm_veterinaria_dev_vnet_name_subnet_app.id
        properties: {
          addressPrefix: '10.0.0.0/24'
          delegations: []
          privateEndpointNetworkPolicies: 'Disabled'
          privateLinkServiceNetworkPolicies: 'Enabled'
        }
      }
    ]
    virtualNetworkPeerings: []
    enableDdosProtection: false
  }
}

resource storageAccounts_stveterinariadev2026_name_resource 'Microsoft.Storage/storageAccounts@2026-04-01' = {
  name: storageAccounts_stveterinariadev2026_name
  location: 'chilecentral'
  sku: {
    name: 'Standard_LRS'
    tier: 'Standard'
  }
  kind: 'StorageV2'
  properties: {
    dualStackEndpointPreference: {
      publishIpv6Endpoint: false
    }
    dnsEndpointType: 'Standard'
    defaultToOAuthAuthentication: false
    publicNetworkAccess: 'Enabled'
    allowCrossTenantReplication: false
    minimumTlsVersion: 'TLS1_2'
    allowBlobPublicAccess: false
    allowSharedKeyAccess: true
    networkAcls: {
      ipv6Rules: []
      bypass: 'AzureServices'
      virtualNetworkRules: []
      ipRules: []
      defaultAction: 'Allow'
    }
    supportsHttpsTrafficOnly: true
    encryption: {
      requireInfrastructureEncryption: false
      services: {
        file: {
          keyType: 'Account'
          enabled: true
        }
        blob: {
          keyType: 'Account'
          enabled: true
        }
      }
      keySource: 'Microsoft.Storage'
    }
    accessTier: 'Hot'
  }
}

resource virtualMachines_vm_veterinaria_dev_name_resource 'Microsoft.Compute/virtualMachines@2025-11-01' = {
  name: virtualMachines_vm_veterinaria_dev_name
  location: 'chilecentral'
  properties: {
    hardwareProfile: {
      vmSize: 'Standard_D2s_v3'
    }
    additionalCapabilities: {
      hibernationEnabled: false
    }
    storageProfile: {
      imageReference: {
        publisher: 'canonical'
        offer: 'ubuntu-24_04-lts'
        sku: 'server'
        version: 'latest'
      }
      osDisk: {
        osType: 'Linux'
        name: '${virtualMachines_vm_veterinaria_dev_name}_disk1_29b300d8c10f4af08a5b4c7ba4a76d1b'
        createOption: 'FromImage'
        caching: 'ReadWrite'
        managedDisk: {
          storageAccountType: 'Premium_LRS'
          id: resourceId(
            'Microsoft.Compute/disks',
            '${virtualMachines_vm_veterinaria_dev_name}_disk1_29b300d8c10f4af08a5b4c7ba4a76d1b'
          )
        }
        deleteOption: 'Delete'
        diskSizeGB: 30
      }
      dataDisks: []
      diskControllerType: 'SCSI'
    }
    osProfile: {
      computerName: virtualMachines_vm_veterinaria_dev_name
      linuxConfiguration: {
        disablePasswordAuthentication: false
        provisionVMAgent: true
        patchSettings: {
          patchMode: 'ImageDefault'
          assessmentMode: 'ImageDefault'
        }
      }
      secrets: []
      allowExtensionOperations: true
      requireGuestProvisionSignal: true
      adminUsername: 'Diegoupn123'
    }
    securityProfile: {
      securityType: 'Standard'
    }
    networkProfile: {
      networkInterfaces: [
        {
          id: networkInterfaces_vm_veterinaria_dev400_name_resource.id
          properties: {
            deleteOption: 'Detach'
          }
        }
      ]
    }
    diagnosticsProfile: {
      bootDiagnostics: {
        enabled: true
      }
    }
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_Default 'Microsoft.DBforMySQL/flexibleServers/advancedThreatProtectionSettings@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'Default'
  properties: {
    state: 'Disabled'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_aad_auth_only 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'aad_auth_only'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_activate_all_roles_on_login 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'activate_all_roles_on_login'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_archive 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'archive'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_audit_log_enabled 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'audit_log_enabled'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_audit_log_events 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'audit_log_events'
  properties: {
    value: 'CONNECTION'
    currentValue: 'CONNECTION'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_audit_log_exclude_users 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'audit_log_exclude_users'
  properties: {
    value: 'azure_superuser'
    currentValue: 'azure_superuser'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_audit_log_include_users 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'audit_log_include_users'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_audit_slow_log_enabled 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'audit_slow_log_enabled'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_auto_generate_certs 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'auto_generate_certs'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_auto_increment_increment 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'auto_increment_increment'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_auto_increment_offset 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'auto_increment_offset'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_autocommit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'autocommit'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_automatic_sp_privileges 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'automatic_sp_privileges'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_avoid_temporal_upgrade 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'avoid_temporal_upgrade'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_azure_replication_repair_time 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'azure_replication_repair_time'
  properties: {
    value: '60'
    currentValue: '60'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_back_log 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'back_log'
  properties: {
    value: '271'
    currentValue: '271'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_big_tables 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'big_tables'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_cache_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_cache_size'
  properties: {
    value: '131072'
    currentValue: '131072'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_checksum 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_checksum'
  properties: {
    value: 'CRC32'
    currentValue: 'CRC32'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_direct_non_transactional_updates 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_direct_non_transactional_updates'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_encryption 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_encryption'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_error_action 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_error_action'
  properties: {
    value: 'ABORT_SERVER'
    currentValue: 'ABORT_SERVER'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_expire_logs_seconds 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_expire_logs_seconds'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_format 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_format'
  properties: {
    value: 'ROW'
    currentValue: 'ROW'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_group_commit_sync_delay 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_group_commit_sync_delay'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_group_commit_sync_no_delay_count 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_group_commit_sync_no_delay_count'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_gtid_simple_recovery 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_gtid_simple_recovery'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_order_commits 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_order_commits'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_rotate_encryption_master_key_at_startup 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_rotate_encryption_master_key_at_startup'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_row_event_max_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_row_event_max_size'
  properties: {
    value: '1048576'
    currentValue: '1048576'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_row_image 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_row_image'
  properties: {
    value: 'minimal'
    currentValue: 'minimal'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_row_metadata 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_row_metadata'
  properties: {
    value: 'MINIMAL'
    currentValue: 'MINIMAL'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_row_value_options 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_row_value_options'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_rows_query_log_events 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_rows_query_log_events'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_stmt_cache_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_stmt_cache_size'
  properties: {
    value: '32768'
    currentValue: '32768'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_transaction_dependency_history_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_transaction_dependency_history_size'
  properties: {
    value: '2000'
    currentValue: '2000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_binlog_transaction_dependency_tracking 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'binlog_transaction_dependency_tracking'
  properties: {
    value: 'WRITESET'
    currentValue: 'WRITESET'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_blackhole 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'blackhole'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_block_encryption_mode 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'block_encryption_mode'
  properties: {
    value: 'aes-128-ecb'
    currentValue: 'aes-128-ecb'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_bulk_insert_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'bulk_insert_buffer_size'
  properties: {
    value: '8388608'
    currentValue: '8388608'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_caching_sha2_password_auto_generate_rsa_keys 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'caching_sha2_password_auto_generate_rsa_keys'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_caching_sha2_password_private_key_path 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'caching_sha2_password_private_key_path'
  properties: {
    value: 'private_key.pem'
    currentValue: 'private_key.pem'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_caching_sha2_password_public_key_path 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'caching_sha2_password_public_key_path'
  properties: {
    value: 'public_key.pem'
    currentValue: 'public_key.pem'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_character_set_filesystem 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'character_set_filesystem'
  properties: {
    value: 'binary'
    currentValue: 'binary'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_character_set_server 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'character_set_server'
  properties: {
    value: 'UTF8MB4'
    currentValue: 'UTF8MB4'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_check_proxy_users 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'check_proxy_users'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_collation_server 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'collation_server'
  properties: {
    value: 'UTF8MB4_0900_AI_CI'
    currentValue: 'UTF8MB4_0900_AI_CI'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_completion_type 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'completion_type'
  properties: {
    value: 'NO_CHAIN'
    currentValue: 'NO_CHAIN'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_concurrent_insert 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'concurrent_insert'
  properties: {
    value: 'AUTO'
    currentValue: 'AUTO'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_connect_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'connect_timeout'
  properties: {
    value: '10'
    currentValue: '10'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_create_admin_listener_thread 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'create_admin_listener_thread'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_cte_max_recursion_depth 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'cte_max_recursion_depth'
  properties: {
    value: '1000'
    currentValue: '1000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_default_authentication_plugin 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'default_authentication_plugin'
  properties: {
    value: 'mysql_native_password'
    currentValue: 'mysql_native_password'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_default_password_lifetime 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'default_password_lifetime'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_default_storage_engine 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'default_storage_engine'
  properties: {
    value: 'InnoDB'
    currentValue: 'InnoDB'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_default_table_encryption 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'default_table_encryption'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_default_tmp_storage_engine 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'default_tmp_storage_engine'
  properties: {
    value: 'InnoDB'
    currentValue: 'InnoDB'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_default_week_format 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'default_week_format'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_delay_key_write 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'delay_key_write'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_delayed_insert_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'delayed_insert_limit'
  properties: {
    value: '100'
    currentValue: '100'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_delayed_insert_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'delayed_insert_timeout'
  properties: {
    value: '300'
    currentValue: '300'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_delayed_queue_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'delayed_queue_size'
  properties: {
    value: '1000'
    currentValue: '1000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_disabled_storage_engines 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'disabled_storage_engines'
  properties: {
    value: 'MyISAM,MRG_MyISAM,BLACKHOLE,FEDEATED,ARCHIVE'
    currentValue: 'MyISAM,MRG_MyISAM,BLACKHOLE,FEDEATED,ARCHIVE'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_disconnect_on_expired_password 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'disconnect_on_expired_password'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_div_precision_increment 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'div_precision_increment'
  properties: {
    value: '4'
    currentValue: '4'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_end_markers_in_json 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'end_markers_in_json'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_enforce_gtid_consistency 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'enforce_gtid_consistency'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_eq_range_index_dive_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'eq_range_index_dive_limit'
  properties: {
    value: '200'
    currentValue: '200'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_error_log_enabled 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'error_log_enabled'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_error_server_log_file 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'error_server_log_file'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_event_scheduler 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'event_scheduler'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'user-override'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_expire_logs_days 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'expire_logs_days'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_explicit_defaults_for_timestamp 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'explicit_defaults_for_timestamp'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_flush 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'flush'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_flush_time 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'flush_time'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_ft_boolean_syntax 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'ft_boolean_syntax'
  properties: {
    value: '+ -><()~*:""&|'
    currentValue: '+ -><()~*:""&|'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_ft_query_expansion_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'ft_query_expansion_limit'
  properties: {
    value: '20'
    currentValue: '20'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_general_log 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'general_log'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_general_log_file 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'general_log_file'
  properties: {
    value: '/app/work2/serverlogs/mysql-general-mysql-veterinaria-dev-2026062603.log'
    currentValue: '/app/work2/serverlogs/mysql-general-mysql-veterinaria-dev-2026062603.log'
    source: 'user-override'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_generated_random_password_length 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'generated_random_password_length'
  properties: {
    value: '20'
    currentValue: '20'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_group_concat_max_len 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'group_concat_max_len'
  properties: {
    value: '1024'
    currentValue: '1024'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_group_replication_consistency 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'group_replication_consistency'
  properties: {
    value: 'EVENTUAL'
    currentValue: 'EVENTUAL'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_gtid_executed_compression_period 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'gtid_executed_compression_period'
  properties: {
    value: '1000'
    currentValue: '1000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_gtid_mode 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'gtid_mode'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_histogram_generation_max_mem_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'histogram_generation_max_mem_size'
  properties: {
    value: '20000000'
    currentValue: '20000000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_host_cache_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'host_cache_size'
  properties: {
    value: '279'
    currentValue: '279'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_information_schema_stats_expiry 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'information_schema_stats_expiry'
  properties: {
    value: '86400'
    currentValue: '86400'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_init_connect 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'init_connect'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_adaptive_flushing 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_adaptive_flushing'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_adaptive_flushing_lwm 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_adaptive_flushing_lwm'
  properties: {
    value: '10'
    currentValue: '10'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_adaptive_hash_index 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_adaptive_hash_index'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_adaptive_hash_index_parts 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_adaptive_hash_index_parts'
  properties: {
    value: '8'
    currentValue: '8'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_adaptive_max_sleep_delay 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_adaptive_max_sleep_delay'
  properties: {
    value: '150000'
    currentValue: '150000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_autoextend_increment 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_autoextend_increment'
  properties: {
    value: '64'
    currentValue: '64'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_autoinc_lock_mode 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_autoinc_lock_mode'
  properties: {
    value: '2'
    currentValue: '2'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_chunk_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_chunk_size'
  properties: {
    value: '134217728'
    currentValue: '134217728'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_dump_at_shutdown 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_dump_at_shutdown'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_dump_now 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_dump_now'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_dump_pct 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_dump_pct'
  properties: {
    value: '100'
    currentValue: '100'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_filename 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_filename'
  properties: {
    value: 'ib_buffer_pool'
    currentValue: 'ib_buffer_pool'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_in_core_file 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_in_core_file'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_instances 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_instances'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_load_abort 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_load_abort'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_load_at_startup 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_load_at_startup'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_load_now 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_load_now'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_buffer_pool_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_buffer_pool_size'
  properties: {
    value: '536870912'
    currentValue: '536870912'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_change_buffer_max_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_change_buffer_max_size'
  properties: {
    value: '25'
    currentValue: '25'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_change_buffering 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_change_buffering'
  properties: {
    value: 'all'
    currentValue: 'all'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_checksum_algorithm 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_checksum_algorithm'
  properties: {
    value: 'crc32'
    currentValue: 'crc32'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_cmp_per_index_enabled 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_cmp_per_index_enabled'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_commit_concurrency 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_commit_concurrency'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_compression_failure_threshold_pct 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_compression_failure_threshold_pct'
  properties: {
    value: '5'
    currentValue: '5'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_compression_level 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_compression_level'
  properties: {
    value: '6'
    currentValue: '6'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_compression_pad_pct_max 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_compression_pad_pct_max'
  properties: {
    value: '50'
    currentValue: '50'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_concurrency_tickets 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_concurrency_tickets'
  properties: {
    value: '5000'
    currentValue: '5000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_data_file_path 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_data_file_path'
  properties: {
    value: 'ibdata1:12M:autoextend'
    currentValue: 'ibdata1:12M:autoextend'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_data_home_dir 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_data_home_dir'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ddl_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ddl_buffer_size'
  properties: {
    value: '1048576'
    currentValue: '1048576'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ddl_threads 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ddl_threads'
  properties: {
    value: '4'
    currentValue: '4'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_deadlock_detect 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_deadlock_detect'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_dedicated_server 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_dedicated_server'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_default_row_format 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_default_row_format'
  properties: {
    value: 'DYNAMIC'
    currentValue: 'DYNAMIC'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_directories 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_directories'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_disable_sort_file_cache 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_disable_sort_file_cache'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_doublewrite 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_doublewrite'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_doublewrite_batch_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_doublewrite_batch_size'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_doublewrite_dir 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_doublewrite_dir'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_doublewrite_files 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_doublewrite_files'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_doublewrite_pages 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_doublewrite_pages'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_fast_shutdown 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_fast_shutdown'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_fatal_semaphore_wait_threshold 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_fatal_semaphore_wait_threshold'
  properties: {
    value: '7201'
    currentValue: '7201'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_file_per_table 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_file_per_table'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_fill_factor 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_fill_factor'
  properties: {
    value: '100'
    currentValue: '100'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_flush_log_at_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_flush_log_at_timeout'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_flush_log_at_trx_commit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_flush_log_at_trx_commit'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_flush_method 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_flush_method'
  properties: {
    value: 'fsync'
    currentValue: 'fsync'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_flush_neighbors 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_flush_neighbors'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_flush_sync 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_flush_sync'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_flushing_avg_loops 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_flushing_avg_loops'
  properties: {
    value: '30'
    currentValue: '30'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_force_load_corrupted 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_force_load_corrupted'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_force_recovery 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_force_recovery'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_fsync_threshold 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_fsync_threshold'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_cache_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_cache_size'
  properties: {
    value: '8000000'
    currentValue: '8000000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_enable_diag_print 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_enable_diag_print'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_enable_stopword 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_enable_stopword'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_max_token_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_max_token_size'
  properties: {
    value: '84'
    currentValue: '84'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_min_token_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_min_token_size'
  properties: {
    value: '3'
    currentValue: '3'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_num_word_optimize 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_num_word_optimize'
  properties: {
    value: '2000'
    currentValue: '2000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_result_cache_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_result_cache_limit'
  properties: {
    value: '2000000000'
    currentValue: '2000000000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_server_stopword_table 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_server_stopword_table'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_sort_pll_degree 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_sort_pll_degree'
  properties: {
    value: '2'
    currentValue: '2'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_total_cache_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_total_cache_size'
  properties: {
    value: '640000000'
    currentValue: '640000000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_ft_user_stopword_table 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_ft_user_stopword_table'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_idle_flush_pct 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_idle_flush_pct'
  properties: {
    value: '100'
    currentValue: '100'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_io_capacity 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_io_capacity'
  properties: {
    value: '200'
    currentValue: '200'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_io_capacity_max 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_io_capacity_max'
  properties: {
    value: '2000'
    currentValue: '2000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_lock_wait_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_lock_wait_timeout'
  properties: {
    value: '50'
    currentValue: '50'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_log_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_log_buffer_size'
  properties: {
    value: '4194304'
    currentValue: '4194304'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_log_checksums 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_log_checksums'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_log_compressed_pages 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_log_compressed_pages'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_log_spin_cpu_abs_lwm 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_log_spin_cpu_abs_lwm'
  properties: {
    value: '80'
    currentValue: '80'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_log_spin_cpu_pct_hwm 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_log_spin_cpu_pct_hwm'
  properties: {
    value: '50'
    currentValue: '50'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_log_wait_for_flush_spin_hwm 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_log_wait_for_flush_spin_hwm'
  properties: {
    value: '400'
    currentValue: '400'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_log_write_ahead_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_log_write_ahead_size'
  properties: {
    value: '16384'
    currentValue: '16384'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_lru_scan_depth 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_lru_scan_depth'
  properties: {
    value: '1024'
    currentValue: '1024'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_max_dirty_pages_pct 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_max_dirty_pages_pct'
  properties: {
    value: '90'
    currentValue: '90'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_max_dirty_pages_pct_lwm 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_max_dirty_pages_pct_lwm'
  properties: {
    value: '10'
    currentValue: '10'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_max_purge_lag 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_max_purge_lag'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_max_purge_lag_delay 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_max_purge_lag_delay'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_max_undo_log_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_max_undo_log_size'
  properties: {
    value: '1073741824'
    currentValue: '1073741824'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_monitor_disable 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_monitor_disable'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_monitor_enable 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_monitor_enable'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_monitor_reset 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_monitor_reset'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_monitor_reset_all 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_monitor_reset_all'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_numa_interleave 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_numa_interleave'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_old_blocks_pct 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_old_blocks_pct'
  properties: {
    value: '37'
    currentValue: '37'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_old_blocks_time 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_old_blocks_time'
  properties: {
    value: '1000'
    currentValue: '1000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_online_alter_log_max_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_online_alter_log_max_size'
  properties: {
    value: '134217728'
    currentValue: '134217728'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_open_files 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_open_files'
  properties: {
    value: '-1'
    currentValue: '-1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_optimize_fulltext_only 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_optimize_fulltext_only'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_page_cleaners 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_page_cleaners'
  properties: {
    value: '4'
    currentValue: '4'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_page_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_page_size'
  properties: {
    value: '16384'
    currentValue: '16384'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_parallel_read_threads 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_parallel_read_threads'
  properties: {
    value: '4'
    currentValue: '4'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_print_all_deadlocks 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_print_all_deadlocks'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_print_ddl_logs 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_print_ddl_logs'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_purge_batch_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_purge_batch_size'
  properties: {
    value: '300'
    currentValue: '300'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_purge_rseg_truncate_frequency 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_purge_rseg_truncate_frequency'
  properties: {
    value: '128'
    currentValue: '128'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_purge_threads 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_purge_threads'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_random_read_ahead 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_random_read_ahead'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_read_ahead_threshold 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_read_ahead_threshold'
  properties: {
    value: '56'
    currentValue: '56'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_read_io_threads 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_read_io_threads'
  properties: {
    value: '4'
    currentValue: '4'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_read_only 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_read_only'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_redo_log_archive_dirs 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_redo_log_archive_dirs'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_redo_log_capacity 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_redo_log_capacity'
  properties: {
    value: '536870912'
    currentValue: '536870912'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_redo_log_encrypt 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_redo_log_encrypt'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_replication_delay 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_replication_delay'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_rollback_on_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_rollback_on_timeout'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_rollback_segments 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_rollback_segments'
  properties: {
    value: '128'
    currentValue: '128'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_sort_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_sort_buffer_size'
  properties: {
    value: '1048576'
    currentValue: '1048576'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_spin_wait_delay 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_spin_wait_delay'
  properties: {
    value: '6'
    currentValue: '6'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_spin_wait_pause_multiplier 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_spin_wait_pause_multiplier'
  properties: {
    value: '50'
    currentValue: '50'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_stats_auto_recalc 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_stats_auto_recalc'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_stats_include_delete_marked 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_stats_include_delete_marked'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_stats_method 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_stats_method'
  properties: {
    value: 'nulls_equal'
    currentValue: 'nulls_equal'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_stats_on_metadata 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_stats_on_metadata'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_stats_persistent 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_stats_persistent'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_stats_persistent_sample_pages 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_stats_persistent_sample_pages'
  properties: {
    value: '20'
    currentValue: '20'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_stats_transient_sample_pages 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_stats_transient_sample_pages'
  properties: {
    value: '8'
    currentValue: '8'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_status_output 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_status_output'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_status_output_locks 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_status_output_locks'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_strict_mode 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_strict_mode'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_sync_array_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_sync_array_size'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_sync_spin_loops 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_sync_spin_loops'
  properties: {
    value: '30'
    currentValue: '30'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_table_locks 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_table_locks'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_temp_data_file_path 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_temp_data_file_path'
  properties: {
    value: 'ibtmp1:12M:autoextend'
    currentValue: 'ibtmp1:12M:autoextend'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_temp_tablespaces_dir 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_temp_tablespaces_dir'
  properties: {
    value: '/app/work/temp'
    currentValue: '/app/work/temp'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_thread_concurrency 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_thread_concurrency'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_thread_sleep_delay 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_thread_sleep_delay'
  properties: {
    value: '10000'
    currentValue: '10000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_tmpdir 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_tmpdir'
  properties: {
    value: '/app/work/temp'
    currentValue: '/app/work/temp'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_undo_directory 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_undo_directory'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_undo_log_encrypt 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_undo_log_encrypt'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_undo_log_truncate 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_undo_log_truncate'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_use_native_aio 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_use_native_aio'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_validate_tablespace_paths 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_validate_tablespace_paths'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_innodb_write_io_threads 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'innodb_write_io_threads'
  properties: {
    value: '4'
    currentValue: '4'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_interactive_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'interactive_timeout'
  properties: {
    value: '28800'
    currentValue: '28800'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_internal_tmp_mem_storage_engine 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'internal_tmp_mem_storage_engine'
  properties: {
    value: 'TempTable'
    currentValue: 'TempTable'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_join_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'join_buffer_size'
  properties: {
    value: '262144'
    currentValue: '262144'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_keep_files_on_create 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'keep_files_on_create'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_key_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'key_buffer_size'
  properties: {
    value: '8388608'
    currentValue: '8388608'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_key_cache_age_threshold 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'key_cache_age_threshold'
  properties: {
    value: '300'
    currentValue: '300'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_key_cache_block_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'key_cache_block_size'
  properties: {
    value: '1024'
    currentValue: '1024'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_key_cache_division_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'key_cache_division_limit'
  properties: {
    value: '100'
    currentValue: '100'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_large_pages 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'large_pages'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_lc_time_names 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'lc_time_names'
  properties: {
    value: 'en_US'
    currentValue: 'en_US'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_local_infile 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'local_infile'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_lock_wait_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'lock_wait_timeout'
  properties: {
    value: '31536000'
    currentValue: '31536000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_bin 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_bin'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'user-override'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_bin_trust_function_creators 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_bin_trust_function_creators'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_bin_use_v1_row_events 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_bin_use_v1_row_events'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_error_services 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_error_services'
  properties: {
    value: 'log_filter_internal; log_sink_internal'
    currentValue: 'log_filter_internal; log_sink_internal'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_error_suppression_list 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_error_suppression_list'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_error_verbosity 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_error_verbosity'
  properties: {
    value: '3'
    currentValue: '3'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_output 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_output'
  properties: {
    value: 'NONE'
    currentValue: 'NONE'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_queries_not_using_indexes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_queries_not_using_indexes'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_raw 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_raw'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_slave_updates 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_slave_updates'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_slow_admin_statements 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_slow_admin_statements'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_slow_extra 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_slow_extra'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_slow_slave_statements 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_slow_slave_statements'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_statements_unsafe_for_binlog 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_statements_unsafe_for_binlog'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_throttle_queries_not_using_indexes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_throttle_queries_not_using_indexes'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_log_timestamps 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'log_timestamps'
  properties: {
    value: 'UTC'
    currentValue: 'UTC'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_long_query_time 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'long_query_time'
  properties: {
    value: '10'
    currentValue: '10'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_low_priority_updates 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'low_priority_updates'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_lower_case_table_names 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'lower_case_table_names'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'user-override'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_mandatory_roles 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'mandatory_roles'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_master_info_repository 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'master_info_repository'
  properties: {
    value: 'TABLE'
    currentValue: 'TABLE'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_master_verify_checksum 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'master_verify_checksum'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_allowed_packet 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_allowed_packet'
  properties: {
    value: '16777216'
    currentValue: '16777216'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_binlog_cache_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_binlog_cache_size'
  properties: {
    value: '18446744073709547520'
    currentValue: '18446744073709547520'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_binlog_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_binlog_size'
  properties: {
    value: '104857600'
    currentValue: '104857600'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_binlog_stmt_cache_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_binlog_stmt_cache_size'
  properties: {
    value: '18446744073709547520'
    currentValue: '18446744073709547520'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_connect_errors 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_connect_errors'
  properties: {
    value: '100'
    currentValue: '100'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_connections 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_connections'
  properties: {
    value: '171'
    currentValue: '171'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_delayed_threads 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_delayed_threads'
  properties: {
    value: '20'
    currentValue: '20'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_digest_length 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_digest_length'
  properties: {
    value: '1024'
    currentValue: '1024'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_error_count 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_error_count'
  properties: {
    value: '1024'
    currentValue: '1024'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_execution_time 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_execution_time'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_heap_table_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_heap_table_size'
  properties: {
    value: '16777216'
    currentValue: '16777216'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_join_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_join_size'
  properties: {
    value: '18446744073709551615'
    currentValue: '18446744073709551615'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_length_for_sort_data 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_length_for_sort_data'
  properties: {
    value: '4096'
    currentValue: '4096'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_points_in_geometry 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_points_in_geometry'
  properties: {
    value: '65536'
    currentValue: '65536'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_prepared_stmt_count 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_prepared_stmt_count'
  properties: {
    value: '16382'
    currentValue: '16382'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_relay_log_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_relay_log_size'
  properties: {
    value: '104857600'
    currentValue: '104857600'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_seeks_for_key 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_seeks_for_key'
  properties: {
    value: '18446744073709551615'
    currentValue: '18446744073709551615'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_sort_length 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_sort_length'
  properties: {
    value: '1024'
    currentValue: '1024'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_sp_recursion_depth 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_sp_recursion_depth'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_user_connections 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_user_connections'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_max_write_lock_count 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'max_write_lock_count'
  properties: {
    value: '18446744073709551615'
    currentValue: '18446744073709551615'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_min_examined_row_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'min_examined_row_limit'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_myisam_sort_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'myisam_sort_buffer_size'
  properties: {
    value: '8388608'
    currentValue: '8388608'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_mysql_native_password_proxy_users 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'mysql_native_password_proxy_users'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_mysqlx 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'mysqlx'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_net_buffer_length 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'net_buffer_length'
  properties: {
    value: '16384'
    currentValue: '16384'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_net_read_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'net_read_timeout'
  properties: {
    value: '120'
    currentValue: '120'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_net_retry_count 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'net_retry_count'
  properties: {
    value: '10'
    currentValue: '10'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_net_write_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'net_write_timeout'
  properties: {
    value: '240'
    currentValue: '240'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_ngram_token_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'ngram_token_size'
  properties: {
    value: '2'
    currentValue: '2'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_offline_mode 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'offline_mode'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_old_alter_table 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'old_alter_table'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_open_files_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'open_files_limit'
  properties: {
    value: '5000'
    currentValue: '5000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_optimizer_prune_level 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'optimizer_prune_level'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_optimizer_search_depth 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'optimizer_search_depth'
  properties: {
    value: '62'
    currentValue: '62'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_optimizer_switch 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'optimizer_switch'
  properties: {
    value: 'default'
    currentValue: 'default'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_optimizer_trace 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'optimizer_trace'
  properties: {
    value: 'enabled=off,one_line=off'
    currentValue: 'enabled=off,one_line=off'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_optimizer_trace_features 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'optimizer_trace_features'
  properties: {
    value: 'greedy_search=on,range_optimizer=on,dynamic_range=on,repeated_subselect=on'
    currentValue: 'greedy_search=on,range_optimizer=on,dynamic_range=on,repeated_subselect=on'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_optimizer_trace_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'optimizer_trace_limit'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_optimizer_trace_max_mem_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'optimizer_trace_max_mem_size'
  properties: {
    value: '1048576'
    currentValue: '1048576'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_optimizer_trace_offset 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'optimizer_trace_offset'
  properties: {
    value: '-1'
    currentValue: '-1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_parser_max_mem_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'parser_max_mem_size'
  properties: {
    value: '18446744073709551615'
    currentValue: '18446744073709551615'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_partial_revokes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'partial_revokes'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_password_history 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'password_history'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_password_require_current 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'password_require_current'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_password_reuse_interval 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'password_reuse_interval'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_stages_current 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_stages_current'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_stages_history 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_stages_history'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_stages_history_long 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_stages_history_long'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_statements_cpu 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_statements_cpu'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_statements_current 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_statements_current'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_statements_history 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_statements_history'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_statements_history_long 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_statements_history_long'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_transactions_current 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_transactions_current'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_transactions_history 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_transactions_history'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_transactions_history_long 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_transactions_history_long'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_waits_current 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_waits_current'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_waits_history 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_waits_history'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_events_waits_history_long 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_events_waits_history_long'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_global_instrumentation 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_global_instrumentation'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_statements_digest 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_statements_digest'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_consumer_thread_instrumentation 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_consumer_thread_instrumentation'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_events_statements_history_long_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_events_statements_history_long_size'
  properties: {
    value: '-1'
    currentValue: '-1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_events_statements_history_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_events_statements_history_size'
  properties: {
    value: '-1'
    currentValue: '-1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_instrument 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_instrument'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_cond_classes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_cond_classes'
  properties: {
    value: '100'
    currentValue: '100'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_digest_length 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_digest_length'
  properties: {
    value: '1024'
    currentValue: '1024'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_file_classes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_file_classes'
  properties: {
    value: '80'
    currentValue: '80'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_file_handles 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_file_handles'
  properties: {
    value: '32768'
    currentValue: '32768'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_memory_classes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_memory_classes'
  properties: {
    value: '450'
    currentValue: '450'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_mutex_classes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_mutex_classes'
  properties: {
    value: '300'
    currentValue: '300'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_rwlock_classes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_rwlock_classes'
  properties: {
    value: '60'
    currentValue: '60'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_socket_classes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_socket_classes'
  properties: {
    value: '10'
    currentValue: '10'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_sql_text_length 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_sql_text_length'
  properties: {
    value: '1024'
    currentValue: '1024'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_stage_classes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_stage_classes'
  properties: {
    value: '175'
    currentValue: '175'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_statement_stack 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_statement_stack'
  properties: {
    value: '10'
    currentValue: '10'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_performance_schema_max_thread_classes 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema_max_thread_classes'
  properties: {
    value: '100'
    currentValue: '100'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_persist_only_admin_x509_subject 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'persist_only_admin_x509_subject'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_persisted_globals_load 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'persisted_globals_load'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_plugin_load 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'plugin_load'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_preload_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'preload_buffer_size'
  properties: {
    value: '32768'
    currentValue: '32768'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_print_identified_with_as_hex 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'print_identified_with_as_hex'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_profiling_history_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'profiling_history_size'
  properties: {
    value: '15'
    currentValue: '15'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_protocol_compression_algorithms 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'protocol_compression_algorithms'
  properties: {
    value: 'zlib,zstd,uncompressed'
    currentValue: 'zlib,zstd,uncompressed'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_query_alloc_block_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'query_alloc_block_size'
  properties: {
    value: '8192'
    currentValue: '8192'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_query_prealloc_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'query_prealloc_size'
  properties: {
    value: '8192'
    currentValue: '8192'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_range_alloc_block_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'range_alloc_block_size'
  properties: {
    value: '4096'
    currentValue: '4096'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_range_optimizer_max_mem_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'range_optimizer_max_mem_size'
  properties: {
    value: '8388608'
    currentValue: '8388608'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_read_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'read_buffer_size'
  properties: {
    value: '131072'
    currentValue: '131072'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_read_only 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'read_only'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_read_rnd_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'read_rnd_buffer_size'
  properties: {
    value: '262144'
    currentValue: '262144'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_regexp_stack_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'regexp_stack_limit'
  properties: {
    value: '8000000'
    currentValue: '8000000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_regexp_time_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'regexp_time_limit'
  properties: {
    value: '32'
    currentValue: '32'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_relay_log 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'relay_log'
  properties: {
    value: '/app/work/relaylogs/relay_bin'
    currentValue: '/app/work/relaylogs/relay_bin'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_relay_log_index 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'relay_log_index'
  properties: {
    value: '/app/work/relaylogs/relay_bin.index'
    currentValue: '/app/work/relaylogs/relay_bin.index'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_relay_log_info_repository 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'relay_log_info_repository'
  properties: {
    value: 'TABLE'
    currentValue: 'TABLE'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_relay_log_purge 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'relay_log_purge'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_relay_log_recovery 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'relay_log_recovery'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_relay_log_space_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'relay_log_space_limit'
  properties: {
    value: '1073741824'
    currentValue: '1073741824'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_replicate_wild_ignore_table 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'replicate_wild_ignore_table'
  properties: {
    value: 'mysql.%,information_schema.%,performance_schema.%,sys.%'
    currentValue: 'mysql.%,information_schema.%,performance_schema.%,sys.%'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_require_secure_transport 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'require_secure_transport'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_rpl_read_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'rpl_read_size'
  properties: {
    value: '8388608'
    currentValue: '8388608'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_rpl_stop_slave_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'rpl_stop_slave_timeout'
  properties: {
    value: '31536000'
    currentValue: '31536000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_schema_definition_cache 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'schema_definition_cache'
  properties: {
    value: '256'
    currentValue: '256'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_secure_file_priv 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'secure_file_priv'
  properties: {
    value: 'NULL'
    currentValue: 'NULL'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_server_id 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'server_id'
  properties: {
    value: '1766244911'
    currentValue: '1766244911'
    source: 'user-override'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_session_track_gtids 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'session_track_gtids'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_session_track_schema 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'session_track_schema'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_session_track_state_change 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'session_track_state_change'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_session_track_system_variables 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'session_track_system_variables'
  properties: {
    value: 'time_zone, autocommit, character_set_client, character_set_results, character_set_connection'
    currentValue: 'time_zone, autocommit, character_set_client, character_set_results, character_set_connection'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_session_track_transaction_info 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'session_track_transaction_info'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sha256_password_auto_generate_rsa_keys 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sha256_password_auto_generate_rsa_keys'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sha256_password_private_key_path 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sha256_password_private_key_path'
  properties: {
    value: 'private_key.pem'
    currentValue: 'private_key.pem'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sha256_password_proxy_users 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sha256_password_proxy_users'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sha256_password_public_key_path 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sha256_password_public_key_path'
  properties: {
    value: 'public_key.pem'
    currentValue: 'public_key.pem'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_show_old_temporals 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'show_old_temporals'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_skip_external_locking 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'skip_external_locking'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_skip_name_resolve 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'skip_name_resolve'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_skip_show_database 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'skip_show_database'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_skip_slave_start 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'skip-slave-start'
  properties: {
    value: 'FALSE'
    currentValue: 'FALSE'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_checkpoint_group 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_checkpoint_group'
  properties: {
    value: '512'
    currentValue: '512'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_checkpoint_period 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_checkpoint_period'
  properties: {
    value: '300'
    currentValue: '300'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_compressed_protocol 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_compressed_protocol'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_exec_mode 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_exec_mode'
  properties: {
    value: 'STRICT'
    currentValue: 'STRICT'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_load_tmpdir 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_load_tmpdir'
  properties: {
    value: '/app/work/temp'
    currentValue: '/app/work/temp'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_max_allowed_packet 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_max_allowed_packet'
  properties: {
    value: '1073741824'
    currentValue: '1073741824'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_net_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_net_timeout'
  properties: {
    value: '60'
    currentValue: '60'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_parallel_type 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_parallel_type'
  properties: {
    value: 'LOGICAL_CLOCK'
    currentValue: 'LOGICAL_CLOCK'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_parallel_workers 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_parallel_workers'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_pending_jobs_size_max 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_pending_jobs_size_max'
  properties: {
    value: '16777216'
    currentValue: '16777216'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_preserve_commit_order 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_preserve_commit_order'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_rows_search_algorithms 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_rows_search_algorithms'
  properties: {
    value: 'TABLE_SCAN,INDEX_SCAN'
    currentValue: 'TABLE_SCAN,INDEX_SCAN'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_skip_errors 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_skip_errors'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_sql_verify_checksum 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_sql_verify_checksum'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_transaction_retries 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_transaction_retries'
  properties: {
    value: '10'
    currentValue: '10'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slave_type_conversions 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slave_type_conversions'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slow_launch_time 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slow_launch_time'
  properties: {
    value: '2'
    currentValue: '2'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slow_query_log 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slow_query_log'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_slow_query_log_file 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'slow_query_log_file'
  properties: {
    value: '/app/serverlogs/slowlogs/mysql-slow-mysql-veterinaria-dev-2026062603.log'
    currentValue: '/app/serverlogs/slowlogs/mysql-slow-mysql-veterinaria-dev-2026062603.log'
    source: 'user-override'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sort_buffer_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sort_buffer_size'
  properties: {
    value: '262144'
    currentValue: '262144'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sql_generate_invisible_primary_key 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sql_generate_invisible_primary_key'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sql_mode 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sql_mode'
  properties: {
    value: 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO'
    currentValue: 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO'
    source: 'user-override'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sql_require_primary_key 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sql_require_primary_key'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_ssl_cipher 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'ssl_cipher'
  properties: {
    value: 'ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-CCM:ECDHE-ECDSA-AES128-CCM:DHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-CCM:DHE-RSA-AES128-CCM'
    currentValue: 'ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-CCM:ECDHE-ECDSA-AES128-CCM:DHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-CCM:DHE-RSA-AES128-CCM'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_stored_program_cache 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'stored_program_cache'
  properties: {
    value: '256'
    currentValue: '256'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_stored_program_definition_cache 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'stored_program_definition_cache'
  properties: {
    value: '256'
    currentValue: '256'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_super_read_only 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'super_read_only'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sync_binlog 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sync_binlog'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sync_master_info 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sync_master_info'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sync_relay_log 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sync_relay_log'
  properties: {
    value: '0'
    currentValue: '0'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sync_relay_log_info 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sync_relay_log_info'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_table_definition_cache 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'table_definition_cache'
  properties: {
    value: '600'
    currentValue: '600'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_table_encryption_privilege_check 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'table_encryption_privilege_check'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_table_open_cache 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'table_open_cache'
  properties: {
    value: '600'
    currentValue: '600'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_table_open_cache_instances 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'table_open_cache_instances'
  properties: {
    value: '16'
    currentValue: '16'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_tablespace_definition_cache 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'tablespace_definition_cache'
  properties: {
    value: '256'
    currentValue: '256'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_temptable_max_ram 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'temptable_max_ram'
  properties: {
    value: '1073741824'
    currentValue: '1073741824'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_temptable_use_mmap 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'temptable_use_mmap'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_thread_cache_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'thread_cache_size'
  properties: {
    value: '9'
    currentValue: '9'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_thread_handling 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'thread_handling'
  properties: {
    value: 'one-thread-per-connection'
    currentValue: 'one-thread-per-connection'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_thread_pool_batch_max_time 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'thread_pool_batch_max_time'
  properties: {
    value: '30000'
    currentValue: '30000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_thread_pool_batch_wait_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'thread_pool_batch_wait_timeout'
  properties: {
    value: '10000'
    currentValue: '10000'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_thread_pool_idle_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'thread_pool_idle_timeout'
  properties: {
    value: '60'
    currentValue: '60'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_thread_pool_new_conn_high_prio 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'thread_pool_new_conn_high_prio'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_thread_pool_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'thread_pool_size'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_thread_pool_stall_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'thread_pool_stall_limit'
  properties: {
    value: '500'
    currentValue: '500'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_thread_stack 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'thread_stack'
  properties: {
    value: '286720'
    currentValue: '286720'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_time_zone 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'time_zone'
  properties: {
    value: '+00:00'
    currentValue: '+00:00'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_tls_ciphersuites 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'tls_ciphersuites'
  properties: {
    value: 'TLS_AES_256_GCM_SHA384:TLS_AES_128_GCM_SHA256:TLS_AES_128_CCM_SHA256'
    currentValue: 'TLS_AES_256_GCM_SHA384:TLS_AES_128_GCM_SHA256:TLS_AES_128_CCM_SHA256'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_tls_version 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'tls_version'
  properties: {
    value: 'TLSv1.2,TLSv1.3'
    currentValue: 'TLSv1.2,TLSv1.3'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_tmp_table_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'tmp_table_size'
  properties: {
    value: '16777216'
    currentValue: '16777216'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_tmpdir 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'tmpdir'
  properties: {
    value: '/app/work/temp'
    currentValue: '/app/work/temp'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_transaction_alloc_block_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'transaction_alloc_block_size'
  properties: {
    value: '8192'
    currentValue: '8192'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_transaction_isolation 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'transaction_isolation'
  properties: {
    value: 'REPEATABLE-READ'
    currentValue: 'REPEATABLE-READ'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_transaction_prealloc_size 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'transaction_prealloc_size'
  properties: {
    value: '4096'
    currentValue: '4096'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_transaction_read_only 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'transaction_read_only'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_transaction_write_set_extraction 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'transaction_write_set_extraction'
  properties: {
    value: 'XXHASH64'
    currentValue: 'XXHASH64'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_updatable_views_with_limit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'updatable_views_with_limit'
  properties: {
    value: 'YES'
    currentValue: 'YES'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_validate_password_check_user_name 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'validate_password_check_user_name'
  properties: {
    value: 'OFF'
    currentValue: 'OFF'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_validate_password_dictionary_file 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'validate_password_dictionary_file'
  properties: {
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_validate_password_length 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'validate_password_length'
  properties: {
    value: '8'
    currentValue: '8'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_validate_password_mixed_case_count 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'validate_password_mixed_case_count'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_validate_password_number_count 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'validate_password_number_count'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_validate_password_policy 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'validate_password_policy'
  properties: {
    value: 'MEDIUM'
    currentValue: 'MEDIUM'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_validate_password_special_char_count 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'validate_password_special_char_count'
  properties: {
    value: '1'
    currentValue: '1'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_validate_user_plugins 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'validate_user_plugins'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_wait_timeout 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'wait_timeout'
  properties: {
    value: '28800'
    currentValue: '28800'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_windowing_use_high_precision 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'windowing_use_high_precision'
  properties: {
    value: 'ON'
    currentValue: 'ON'
    source: 'system-default'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_information_schema 'Microsoft.DBforMySQL/flexibleServers/databases@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'information_schema'
  properties: {
    charset: 'utf8mb3'
    collation: 'utf8mb3_general_ci'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_mysql 'Microsoft.DBforMySQL/flexibleServers/databases@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'mysql'
  properties: {
    charset: 'utf8mb4'
    collation: 'utf8mb4_0900_ai_ci'
  }
}

resource Microsoft_DBforMySQL_flexibleServers_databases_flexibleServers_mysql_veterinaria_dev_name_performance_schema 'Microsoft.DBforMySQL/flexibleServers/databases@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'performance_schema'
  properties: {
    charset: 'utf8mb4'
    collation: 'utf8mb4_0900_ai_ci'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_sys 'Microsoft.DBforMySQL/flexibleServers/databases@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'sys'
  properties: {
    charset: 'utf8mb4'
    collation: 'utf8mb4_0900_ai_ci'
  }
}

resource flexibleServers_mysql_veterinaria_dev_name_ClientIPAddress_2026_6_25_22_23_25 'Microsoft.DBforMySQL/flexibleServers/firewallRules@2025-06-01-preview' = {
  parent: flexibleServers_mysql_veterinaria_dev_name_resource
  name: 'ClientIPAddress_2026-6-25_22-23-25'
  properties: {
    startIpAddress: '38.25.15.19'
    endIpAddress: '38.25.15.19'
  }
}

resource networkSecurityGroups_vm_veterinaria_dev_nsg_name_Allow_Tomcat_8080 'Microsoft.Network/networkSecurityGroups/securityRules@2025-05-01' = {
  name: '${networkSecurityGroups_vm_veterinaria_dev_nsg_name}/Allow-Tomcat-8080'
  properties: {
    protocol: 'TCP'
    sourcePortRange: '*'
    destinationPortRange: '8080'
    sourceAddressPrefix: '*'
    destinationAddressPrefix: '*'
    access: 'Allow'
    priority: 1010
    direction: 'Inbound'
    sourcePortRanges: []
    destinationPortRanges: []
    sourceAddressPrefixes: []
    destinationAddressPrefixes: []
  }
  dependsOn: [
    networkSecurityGroups_vm_veterinaria_dev_nsg_name_resource
  ]
}

resource networkSecurityGroups_vm_veterinaria_dev_nsg_name_SSH 'Microsoft.Network/networkSecurityGroups/securityRules@2025-05-01' = {
  name: '${networkSecurityGroups_vm_veterinaria_dev_nsg_name}/SSH'
  properties: {
    protocol: 'TCP'
    sourcePortRange: '*'
    destinationPortRange: '22'
    sourceAddressPrefix: '*'
    destinationAddressPrefix: '*'
    access: 'Allow'
    priority: 300
    direction: 'Inbound'
    sourcePortRanges: []
    destinationPortRanges: []
    sourceAddressPrefixes: []
    destinationAddressPrefixes: []
  }
  dependsOn: [
    networkSecurityGroups_vm_veterinaria_dev_nsg_name_resource
  ]
}

resource virtualNetworks_vm_veterinaria_dev_vnet_name_subnet_app 'Microsoft.Network/virtualNetworks/subnets@2025-05-01' = {
  name: '${virtualNetworks_vm_veterinaria_dev_vnet_name}/subnet-app'
  properties: {
    addressPrefix: '10.0.0.0/24'
    delegations: []
    privateEndpointNetworkPolicies: 'Disabled'
    privateLinkServiceNetworkPolicies: 'Enabled'
  }
  dependsOn: [
    virtualNetworks_vm_veterinaria_dev_vnet_name_resource
  ]
}

resource storageAccounts_stveterinariadev2026_name_default 'Microsoft.Storage/storageAccounts/blobServices@2026-04-01' = {
  parent: storageAccounts_stveterinariadev2026_name_resource
  name: 'default'
  sku: {
    name: 'Standard_LRS'
    tier: 'Standard'
  }
  properties: {
    staticWebsite: {
      enabled: false
    }
    containerDeleteRetentionPolicy: {
      enabled: true
      days: 7
    }
    cors: {
      corsRules: []
    }
    deleteRetentionPolicy: {
      allowPermanentDelete: false
      enabled: true
      days: 7
    }
  }
}

resource Microsoft_Storage_storageAccounts_fileServices_storageAccounts_stveterinariadev2026_name_default 'Microsoft.Storage/storageAccounts/fileServices@2026-04-01' = {
  parent: storageAccounts_stveterinariadev2026_name_resource
  name: 'default'
  sku: {
    name: 'Standard_LRS'
    tier: 'Standard'
  }
  properties: {
    protocolSettings: {
      smb: {
        encryptionInTransit: {
          required: true
        }
      }
    }
    cors: {
      corsRules: []
    }
    shareDeleteRetentionPolicy: {
      enabled: true
      days: 7
    }
  }
}

resource Microsoft_Storage_storageAccounts_queueServices_storageAccounts_stveterinariadev2026_name_default 'Microsoft.Storage/storageAccounts/queueServices@2026-04-01' = {
  parent: storageAccounts_stveterinariadev2026_name_resource
  name: 'default'
  properties: {
    cors: {
      corsRules: []
    }
  }
}

resource Microsoft_Storage_storageAccounts_tableServices_storageAccounts_stveterinariadev2026_name_default 'Microsoft.Storage/storageAccounts/tableServices@2026-04-01' = {
  parent: storageAccounts_stveterinariadev2026_name_resource
  name: 'default'
  properties: {
    cors: {
      corsRules: []
    }
  }
}

resource networkInterfaces_vm_veterinaria_dev400_name_resource 'Microsoft.Network/networkInterfaces@2025-05-01' = {
  name: networkInterfaces_vm_veterinaria_dev400_name
  location: 'chilecentral'
  kind: 'Regular'
  properties: {
    ipConfigurations: [
      {
        name: 'ipconfig1'
        id: '${networkInterfaces_vm_veterinaria_dev400_name_resource.id}/ipConfigurations/ipconfig1'
        properties: {
          privateIPAddress: '10.0.0.4'
          privateIPAllocationMethod: 'Dynamic'
          publicIPAddress: {
            id: publicIPAddresses_vm_veterinaria_dev_ip_name_resource.id
            properties: {
              deleteOption: 'Detach'
            }
          }
          subnet: {
            id: virtualNetworks_vm_veterinaria_dev_vnet_name_subnet_app.id
          }
          primary: true
          privateIPAddressVersion: 'IPv4'
        }
      }
    ]
    dnsSettings: {
      dnsServers: []
    }
    enableAcceleratedNetworking: true
    enableIPForwarding: false
    disableTcpStateTracking: false
    networkSecurityGroup: {
      id: networkSecurityGroups_vm_veterinaria_dev_nsg_name_resource.id
    }
    nicType: 'Standard'
    auxiliaryMode: 'None'
    auxiliarySku: 'None'
  }
}
