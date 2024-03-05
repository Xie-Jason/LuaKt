do
    v = 1
    t = {
        ["k"] = v,
        j = v
    }

    local n = 0

    if n > 0 then
        local v1,v2 = 3,4
        n = 10
    elseif n < 10 then
        n = 20
    else
        local v1 = ""
        n = 30
    end
end
--
local n1,n2,n3,n4 = 1,2,3,4
--
do
    local n1,n2,n3,n4 = 1,2,3,4
    local s1,s2,s3,s4 = "","","",""
    function test()
        local myS1 = s2
        local myS2 = s3 + ""
    end

    function test2()
        local n = n1 + n4
    end
end

local s1,s2,s3,s4 = "","","",""




repeat
    n = n - 1
    local v1, v2 = 1,2
until n > 0