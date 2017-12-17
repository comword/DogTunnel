package findgo
import (
	"fmt"
	"io/ioutil"
	"strings"
	"net/http"
	"time"
	"sync"
)

var wg sync.WaitGroup
var Figtag string

func doreq(addr string, ret chan string) {
	defer wg.Done()
	client := http.Client{
		Timeout: 4 * time.Second,
	}
	res, err := client.Get("http://"+addr)
	if err != nil {
		return
	}
	reply, err := ioutil.ReadAll(res.Body)
	res.Body.Close()
	if err != nil {
		return
	}
	if strings.Contains(string(reply),Figtag) {
		ret <- addr
	}
}
func Dofind(m_range string) string{
	//Figtag("LuCI - Lua Configuration Interface - SIX")
	chann := make(chan string, 1)
	for i:=2; i<254; i++ {
		wg.Add(1)
		addr := fmt.Sprintf(m_range,i); //"172.26.32.%d"
		go doreq(addr, chann)
	}
	wg.Wait()
	select {
		case msg := <-chann:
        		return msg
		default:
			return ""
	}
}
