[CmdletBinding()]
Param(
   [Parameter(Mandatory=$True,Position=1)]
   [string]$ipWebAdmin=""
)

$iplisten = netsh http show iplisten
$regex = '\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b'
if ($iplisten | select-string -pattern $regex)
{
    if ($iplisten | select-string -pattern $ipWebAdmin)
    {
        # iplisten table exists and contains ipWebAdmin
        # first, remove ipWebAdmin
        netsh http delete iplisten ipaddress=$ipWebAdmin
        # re-read iplisten table, check if there are any IPs left
        $iplisten = netsh http show iplisten
        if (!($iplisten | select-string -pattern $regex))
        {
            # iplisten table is empty
            # bind all IPs on the machine to IIS, except ipWebAdmin
            Foreach ($ip in (gwmi Win32_NetworkAdapterConfiguration | ? { $_.IPAddress -ne $null }).ipaddress)
            {
	            if ($ip -ne $ipWebAdmin)
	            {
		            netsh http add iplisten ipaddress=$ip
	            }
            }
        }
    }
    else
    {
        # iplisten table contains other IPs, but not ipWebAdmin
        # all well, nothing to do
    }
}
else
{
    # iplisten table is empty
    # bind all IPs on the machine to IIS, except ipWebAdmin
    Foreach ($ip in (gwmi Win32_NetworkAdapterConfiguration | ? { $_.IPAddress -ne $null }).ipaddress)
    {
	    if ($ip -ne $ipWebAdmin)
	    {
		    netsh http add iplisten ipaddress=$ip
	    }
    }
}

# re-read iplisten table
$iplisten = netsh http show iplisten

# if iplisten is still empty (likely because there is only one IP on the machine), add a dummy IP to iplisten
if (!($iplisten | select-string -pattern $regex))
{
    netsh http add iplisten ipaddress="127.0.0.1"
}