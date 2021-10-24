--
-- Created by IntelliJ IDEA.
-- User: rechard
-- Date: 2021/10/19
-- To change this template use File | Settings | File Templates.

local ratelimit_info=redis.call("HMGET",KEYS[1],"last_access_sec","curr_permits","max_burst","rate")
local last_access_sec=ratelimit_info[1]
local curr_permits=tonumber(ratelimit_info[2])
local max_burst=tonumber(ratelimit_info[3])
local rate=tonumber(ratelimit_info[4])

local local_curr_permits=max_burst
--local a=redis.call('TIME')
--local now=(a[1]*1000000+a[2])/1000000000 --精确得到秒
local now=KEYS[2]

if (type(last_access_sec) ~='boolean' and last_access_sec ~=nil) then
  local reverse_permits=math.floor(now-last_access_sec)*rate
    print(reverse_permits)
  if(reverse_permits>0) then
     redis.call("HSET",KEYS[1],"last_access_sec",now)
  end
  local expect_curr_permits=reverse_permits+curr_permits
  local_curr_permits=math.min(expect_curr_permits,max_burst)
else
  redis.call("HMSET",KEYS[1],"last_mill_sec",now)
end

local result=-1
if(local_curr_permits>=1) then
 result=1
 redis.call("HSET",KEYS[1],"curr_permits",local_curr_permits-1)
else
 redis.call("HSET",KEYS[1],"curr_permits",local_curr_permits)
end

return result