/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package performancetest;

/**
 *
 * @author mahshidhelalimoghadam
 */
public class VirtualMachine {
    public Double Throughput;
    public Double ResponseTime_i;
    public Double ResponseTime;
    public Double NormalizedResponsetime;
    public Double Requirement_ResTime;
    public Double Acceptolerance; // interms of percentage of requirement response time
    // 4 cores with 2.5 GHz
    public double VM_CPU_g;
    public double VM_Mem_g;
    public double VM_Disk_g;
    public double VM_CPU_i;
    public double VM_Mem_i;
    public double VM_Disk_i;
    public Double VM_CPUtil;
    public Double VM_MemUtil;
    public Double VM_DiskUtil;
    public Double[] VM_SensitivityValues;
    
    public void CalculateVMThroughput_ResponseTime()
    {
         // in terms of ms
            
            Double Part1 = (double)(this.VM_CPU_g / this.VM_CPU_i) * this.VM_SensitivityValues[0];
            Double Part2 = (double)(this.VM_Mem_g / this.VM_Mem_i) * this.VM_SensitivityValues[1];
            Double Part3 = (double)(this.VM_Disk_g / this.VM_Disk_i) * this.VM_SensitivityValues[2];
            Double Part4 = this.VM_SensitivityValues[0] + this.VM_SensitivityValues[1] + this.VM_SensitivityValues[2];
            this.Throughput = ((Part1+Part2+Part3)/Part4)* 1000.0/this.ResponseTime_i;
            this.ResponseTime=(double) Math.round((1000.0/this.Throughput)*100.0)/100.0;
        
    }
    
    public void CalculateCPUtilImprov()
    {
        
        this.VM_CPUtil= this.VM_CPU_i/this.VM_CPU_g;
        
    }
    
    public void CalculateMemUtilImprov()
    {
        
        this.VM_MemUtil= this.VM_Mem_i/this.VM_Mem_g;
        
    }
    
    public void CalculateDiskUtilImprov()
    {
        
        this.VM_DiskUtil= this.VM_Disk_i/this.VM_Disk_g;
        
    }
    
    public void NormalizeResponseTime()
    {
        // R_Norm= b.2/Math.Pi
        this.NormalizedResponsetime= (2.0/Math.PI) * Math.atan((double)(this.ResponseTime/this.Requirement_ResTime));
        
    }
    
    
}
