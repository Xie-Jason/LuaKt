-- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  n0:uv0  
local n0 = 0
wrap = function()
    -- fnId : 0 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  n1:lv3  n2:lv4  hello:lv5  
    local n1, n2 = 1, 2, 3
    local hello = function()
        -- fnId : 1  UpValues : lv3$n0 <- uv0$n0, lv4$n1 <- lv3$n1, lv5$n2 <- lv4$n2 
        -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  n0:lv3  n1:lv4  n2:lv5  
        print("Hello", n0, n1, n2)
    end
    hello()
end
print("参数1", "参数2", "参数3")
print("22,11,33,444"["split"]("22,11,33,444", ","))
wrap()
loop = function()
    -- fnId : 2 
    -- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  i:lv3  
    local i = 10
    while (i < 10) do
        i = (i + 1)
    end
end
