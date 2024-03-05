-- <ThisModule>:lv0  <Arguments>:lv1  <UpValues>:lv2  width:lv3  height:lv4  wscale:lv5  m:lv6  limit2:lv7  
-- write:lv8  char:lv9  
local width = (tonumber((arg and arg[1])) or 100)
local height, wscale = width, (2 / width)
local m, limit2 = 50, 4.0
local write, char = io["write"], string["char"]
write("P4\n", width, " ", height, "\n")
for y = 0, (height - 1), 1 do
    -- y:lv10  Ci:lv11  
    local Ci = ((2 * y / height) - 1)
    for xb = 0, (width - 1), 8 do
        -- xb:lv12  bits:lv13  xbb:lv14  
        local bits = 0
        local xbb = (xb + 7)
        for x = xb, ((xbb < width) and xbb or (width - 1)), 1 do
            -- x:lv15  Zr:lv16  Zi:lv17  Zrq:lv18  Ziq:lv19  Cr:lv20  
            bits = (bits + bits)
            local Zr, Zi, Zrq, Ziq = 0.0, 0.0, 0.0, 0.0
            local Cr = ((x * wscale) - 1.5)
            for i = 1, m, 1 do
                -- i:lv21  Zri:lv22  
                local Zri = (Zr * Zi)
                Zr = (Zrq - Ziq + Cr)
                Zi = (Zri + Zri + Ci)
                Zrq = (Zr * Zr)
                Ziq = (Zi * Zi)
                if ((Zrq + Ziq) > limit2) then
                    bits = (bits + 1)
                    break
                end
            end
        end
        if (xbb >= width) then
            for x = width, xbb, 1 do
                -- x:lv15  
                bits = (bits + bits + 1)
            end
        end
        write(char((255 - bits)))
    end
end
