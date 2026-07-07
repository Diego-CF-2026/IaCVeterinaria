param virtualMachines_vm_veterinaria_test_name string = 'vm-veterinaria-test'
param storageAccounts_stvetsctest2026xx_name string = 'stvetsctest2026xx'
param networkInterfaces_vm_veterinaria_test400_name string = 'vm-veterinaria-test400'
param publicIPAddresses_vm_veterinaria_test_ip_name string = 'vm-veterinaria-test-ip'
param virtualNetworks_vm_veterinaria_test_vnet_name string = 'vm-veterinaria-test-vnet'
param flexibleServers_mysql_vet_sc_test2026_name string = 'mysql-vet-sc-test2026'
param networkSecurityGroups_vm_veterinaria_test_nsg_name string = 'vm-veterinaria-test-nsg'

@secure()
param adminPassword string = 'VeterinariaPassword2026*'

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
    }
  }
}

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
    subnets: [
      {
        name: 'subnet-app'
        properties: {
          addressPrefix: '10.0.0.0/24'
        }
      }
    ]
  }
}

resource storageAccounts_stvetsctest2026xx_resource 'Microsoft.Storage/storageAccounts@2026-04-01' = {
  name: storageAccounts_stvetsctest2026xx_name
  location: 'chilecentral'
  sku: {
    name: 'Standard_LRS'
  }
  kind: 'StorageV2'
  properties: {
    minimumTlsVersion: 'TLS1_2'
    allowBlobPublicAccess: false
    supportsHttpsTrafficOnly: true
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
    storageProfile: {
      imageReference: {
        publisher: 'canonical'
        offer: 'ubuntu-24_04-lts'
        sku: 'server'
        version: 'latest'
      }
      osDisk: {
        osType: 'Linux'
        name: '${virtualMachines_vm_veterinaria_test_name}_disk1_test'
        createOption: 'FromImage'
        caching: 'ReadWrite'
        managedDisk: {
          storageAccountType: 'Premium_LRS'
        }
        diskSizeGB: 30
      }
    }
    osProfile: {
      computerName: virtualMachines_vm_veterinaria_test_name
      adminUsername: 'Diegoupn123'
      adminPassword: adminPassword
      linuxConfiguration: {
        disablePasswordAuthentication: false
        provisionVMAgent: true
      }
    }
    networkProfile: {
      networkInterfaces: [
        {
          id: networkInterfaces_vm_veterinaria_test400_resource.id
        }
      ]
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
          primary: true
          privateIPAddressVersion: 'IPv4'
        }
      }
    ]
    networkSecurityGroup: {
      id: networkSecurityGroups_vm_veterinaria_test_nsg_resource.id
    }
  }
}