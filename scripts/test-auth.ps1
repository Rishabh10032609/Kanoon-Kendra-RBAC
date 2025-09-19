Param(
  [string]$BaseUrl = "http://localhost:8080",
  [string]$Username = "john",
  [string]$Password = "Password@123",
  [string]$DeviceId = "web-1"
)

function Invoke-PostJson {
  param(
    [string]$Url,
    [hashtable]$Body,
    [hashtable]$Headers
  )
  $json = ($Body | ConvertTo-Json -Depth 5)
  if (-not $Headers) { $Headers = @{} }
  $Headers["Content-Type"] = "application/json"
  return Invoke-RestMethod -Method Post -Uri $Url -Headers $Headers -Body $json
}

Write-Host "Registering user $Username ..."
try {
  $register = Invoke-PostJson -Url "$BaseUrl/api/auth/register" -Body @{ username=$Username; password=$Password; deviceId=$DeviceId }
  Write-Host "Registered. AccessToken length:" ($register.accessToken | Measure-Object -Character).Characters
} catch {
  Write-Warning "Register may have failed (possibly already exists): $($_.Exception.Message)"
}

Write-Host "Logging in user $Username ..."
$login = Invoke-PostJson -Url "$BaseUrl/api/auth/login" -Body @{ username=$Username; password=$Password; deviceId=$DeviceId }
$access = $login.accessToken
$refresh = $login.refreshToken
Write-Host "Login OK. Access token (first 32 chars):" ($access.Substring(0, [Math]::Min(32, $access.Length)))

Write-Host "Calling protected /api/hello ..."
$hello = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/hello" -Headers @{ Authorization = "Bearer $access" }
Write-Host "Hello response:" $hello

Write-Host "Refreshing access token ..."
$refreshed = Invoke-PostJson -Url "$BaseUrl/api/auth/refresh" -Body @{ refreshToken=$refresh }
$newAccess = $refreshed.accessToken
Write-Host "New access token (first 32 chars):" ($newAccess.Substring(0, [Math]::Min(32, $newAccess.Length)))

Write-Host "Logging out (device-specific) ..."
Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/auth/logout?deviceId=$DeviceId" -Headers @{ Authorization = "Bearer $newAccess" } | Out-Null
Write-Host "Logged out device $DeviceId."
