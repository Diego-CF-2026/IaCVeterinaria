param virtualMachines_vm_veterinaria_test_name string = 'vm-veterinaria-test'
param storageAccounts_stvetsctest2026xx_name string = 'stvetsctest2026xx'
param networkInterfaces_vm_veterinaria_test400_name string = 'vm-veterinaria-test400'
param publicIPAddresses_vm_veterinaria_test_ip_name string = 'vm-veterinaria-test-ip'
param virtualNetworks_vm_veterinaria_test_vnet_name string = 'vm-veterinaria-test-vnet'
param flexibleServers_mysql_vet_sc_test2026_name string = 'mysql-vet-sc-test2026'
param networkSecurityGroups_vm_veterinaria_test_nsg_name string = 'vm-veterinaria-test-nsg'

resource flexibleServers_mysql_vet_sc_test2026_resource 'Microsoft.DBforMySQL/flexibleServers@2025-06-01-preview' = {
  name: flexibleServers_mysql_vet_sc_test2026_name
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

// REGLA DE FIREWALL AUTOMÁTICA ADICIONADA PARA CONEXIÓN EXTRAS/INTERNAS DE AZURE
resource mysql_Firewall_Allow_Azure 'Microsoft.DBforMySQL/flexibleServers/firewallRules@2025-06-01-preview' = {
  parent: flexibleServers_mysql_vet_sc_test2026_resource
  name: 'allow-azure-internal-services'
  properties: {
    startIpAddress: '0.0.0.0'
    endIpAddress: '0.0.0.0'
  }
}

resource networkSecurityGroups_vm_veterinaria_test_nsg_resource 'Microsoft.Network/networkSecurityGroups@2025-05-01' = {
  name: networkSecurityGroups_vm_veterinaria_test_nsg_name
  location: 'chilecentral'
  properties: {
    securityRules: [
      {
        name: 'SSH'
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

resource publicIPAddresses_vm_veterinaria_test_ip_resource 'Microsoft.Network/publicIPAddresses@2025-05-01' = {
  name: publicIPAddresses_vm_veterinaria_test_ip_name
  location: 'chilecentral'
  sku: {
    name: 'Standard'
    tier: 'Regional'
  }
  properties: {
    publicIPAddressVersion: 'IPv4'
    publicIPAllocationMethod: 'Static'
    idleTimeoutInMinutes: 4
    ipTags: []
    ddosSettings: {
      protectionMode: 'VirtualNetworkInherited'
    }
  }
}

resource virtualNetworks_vm_veterinaria_test_vnet_resource 'Microsoft.Network/virtualNetworks@2025-05-01' = {
  name: virtualNetworks_vm_veterinaria_test_vnet_name
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

resource storageAccounts_stvetsctest2026xx_resource 'Microsoft.Storage/storageAccounts@2026-04-01' = {
  name: storageAccounts_stvetsctest2026xx_name
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

resource virtualMachines_vm_veterinaria_test_resource 'Microsoft.Compute/virtualMachines@2025-11-01' = {
  name: virtualMachines_vm_veterinaria_test_name
  location: 'chilecentral'
  dependsOn: [
    networkInterfaces_vm_veterinaria_test400_resource
  ]
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
        name: '${virtualMachines_vm_veterinaria_test_name}_disk1_test_29b300d8c1'
        createOption: 'FromImage'
        caching: 'ReadWrite'
        managedDisk: {
          storageAccountType: 'Premium_LRS'
        }
        deleteOption: 'Delete'
        diskSizeGB: 30
      }
      dataDisks: []
      diskControllerType: 'SCSI'
    }
    osProfile: {
      computerName: virtualMachines_vm_veterinaria_test_name
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
          id: networkInterfaces_vm_veterinaria_test400_resource.id
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

resource networkInterfaces_vm_veterinaria_test400_resource 'Microsoft.Network/networkInterfaces@2025-05-01' = {
  name: networkInterfaces_vm_veterinaria_test400_name
  location: 'chilecentral'
  kind: 'Regular'
  properties: {
    ipConfigurations: [
      {
        name: 'ipconfig1'
        properties: {
          privateIPAllocationMethod: 'Dynamic'
          publicIPAddress: {
            id: publicIPAddresses_vm_veterinaria_test_ip_resource.id
          }
          subnet: {
            id: '${virtualNetworks_vm_veterinaria_test_vnet_resource.id}/subnets/subnet-app'
          }
        }
      }
    ]
    networkSecurityGroup: {
      id: networkSecurityGroups_vm_veterinaria_test_nsg_resource.id
    }
  }
}

// Configuración básica del servidor MySQL
resource config_aad_auth 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_vet_sc_test2026_resource
  name: 'aad_auth_only'
  properties: { value: 'OFF' }
}
resource config_roles 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_vet_sc_test2026_resource
  name: 'activate_all_roles_on_login'
  properties: { value: 'ON' }
}
resource config_autocommit 'Microsoft.DBforMySQL/flexibleServers/configurations@2025-06-01-preview' = {
  parent: flexibleServers_mysql_vet_sc_test2026_resource
  name: 'autocommit'
  properties: { value: 'ON' }
}