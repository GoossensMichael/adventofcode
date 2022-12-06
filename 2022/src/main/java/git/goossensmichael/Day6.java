package git.goossensmichael;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Day6 {

    public static void main(final String[] args) {
        final String input = INPUT;

        {
            final int desiredWindowSize = 4;
            findStartOfMessageMarker(input, desiredWindowSize);
        }

        System.out.println();

        {
            final int desiredWindowSize = 14;
            findStartOfMessageMarker(input, desiredWindowSize);
        }
    }

    private static void findStartOfMessageMarker(final String input, final int desiredWindowSize) {
        int i = desiredWindowSize;
        while (i < input.length() && !isUniqueWindow(input, i - desiredWindowSize, i, desiredWindowSize)) {
            i++;
        }

        System.out.println(i + " " + input.substring(i - desiredWindowSize, i));
    }

    private static boolean isUniqueWindow(final String input, final int begin, final int end,
                                          final int desiredWindowSize) {
        return input.substring(begin, end).chars()
                .boxed()
                .collect(Collectors.toSet())
                .size() == desiredWindowSize;
    }

    private static final String TST_INPUT =
            """
            mjqjpqmgbljsphdztnvjfqwrcgsmlb
            """;

    private static final String INPUT = "vftfrfcrfrpptffhnnnsznzgngqgsssczcjcdcssmgmtmnmqnmmfttbdttqtggmgpmmqrqzrzttptpdtpddqfdffgjgzjzczjcjjvttvmmrwmrwwjtwjttnccgbcbjbgjjcvvsqshszhzvhvsstjjljcczmzjmzzdtzzhfhvvvflljtllfqqdmmmhmfhffzhhtgtssqppgcppfjjrnngwwrvrvqvpppcspsrrsqqqlmmpcmcjmmjhmhzzpnnggmjjrprptpccdggcrrwfwggsfggbbqlqflqqcsqqccqzqbbnbmmlzllnhnwwsjsgstgttfdtffzfvzvbzzrlzlppcsctcncrncnjnfnqnvngnzzscslsppdqdbbthhqvqfvqqvccvdcddsllnwnqwwsccjmcjmmfmppnzpzffslffjrfjftftrffwcwppfsftssdwdzzffnwndnvnggbbfbgfbbfmbbjzzdqdtqtfqqpzzqpzpllvrrbqqbcqqzvznzmmwcwtwftwfwtftvtddhldldblbzlbzlllcjjwllvcllqplqplpzlzqzsqzsznnqwnqqrcqqnrrhmmqbqqlbqqrhrrdgrrsqssnwsnswsgsfsbbdhbdbqqtsqsbsmmbtmmghhcththrrnvvnlvvdvwwjwmwbmmvtvztzmtmrmprpvvmbmrbrsbbpsbbtccwgccbctbccjrjcjtccrrdnnwwqrwwjpjtptcpcfpfvfjjtqtnqqvzqqfhfrftrrgqgbqqtwwthwhqwqjqmmlglnlrnlrnnshsmspmmfnnqwqgwqqprpbbjbhjbbfqftfwwjhhcpcgcpcvppgfgcfgffbzbfzbzqzccmppndppspbssnbbpdpjpgpngpnggtztjzjdjhjmmwrmmqhhcvvvltvllvmvpvlplwplpmllgpllczlzpzczwzswwqgqbqppcjjhjddgccfmmctcjcscczzzjzwzjjsccdmdppdrdccmttjpttqvqdqtdqtqhqchhhtbbddmhhwdhdwdjdccnhcnndpdldvldlvddbjdbjbppjbpbssqnqnmmmclcggrmmdnnwmwddvjvdvrvzzglzlnnllhqqrbbcvvdtttdvvnfnqqstszsjsrscspszppfjjfttgqttdpdbbbvnndbdcdqdrdhhdhhdjjjzzhqqrcrvccrvrtvtnvnpppccpdpccjgjmjzzbsbgsbstsbbtdttrqtrrsrdrdcdjcddbcbjcbjbwbbslbbbbnmmtsmmrwrnwwqtqzqpprhrzhrhhldddrpdrpdpwdpwdpwwlmmzssbwswppldlmddphhnfhhczhhqrrdgdjjdttztftzffdvddqnqvvlbbncnffssbnbfnnzbnznhzhdzzrqzzptzppsnsnzntzzfwfzzvrzrbzrbzrbzrrqqltqlqppbwppmvpmvpmmbnnbhbmbdmbbhmmngmgmhgmghhvttzzfvfggrrchrrbffzjzlzsllbqqhqrrmqrrlzzsqzqqmhhmnmtntstnsnhsnnwwpdphhgjhjccbcqcjcbjbttmssqffjzjmzmqqrbbfnffcbfcfzfrzztftntbntttlddvwdvdcdhcdhdbbjfjzjwpbcvlqvcwjrcjssdfmgwrhrjvhpgqsbtzqqdwjrqsjplqjdzdcrtvqlcrfpcgwpjnbpcmbwnwbzhcvjvzzpvqnzdqdgpfrvdpfdmpprmzmghdfjjzfqjqcbplwntzmsrpqclgrqzhlsgwffqqntswnjsmrcpjlsvdrmcwdgqzsbsbvhbszqgwqffcgbqmjrfjdvbpwbrzwbjgvvjchwfscrhrtzbghjlcnsqqhdgqtdcqrrpsbzqvjwptblszrtffhwcvbngnsdjgpscfzwrncwlpfqgwdzffsqmjcbrlffftpvhjchmgmgqvjnpfsjnfzddqjsfqcpjgfrfgrtlmtfqphjfmdcvmghrdqvbbbhlstgpcgmqnwpdjwbrdbntbpncnztmnmzmsjzrwjmccqslngrvbjcjjgcvnvhsslfhwpwtjjcwgzqpdvqrlbttnnwdphztmwcdlvlqggrdprmzdfpbfhmsgqznzjhdqpmhfphqcvbfqmhnfpcvstrhdbtmljnnhqtfnpdwnszfrflsbbqjsvbvggzfhlcljwlrlfnlwlzzllzbqftlwzqsvwlldslnfnnbhlwmhqhrjlqzpdsjlhjncpwtnpnqvjtzzjnmdntmbjbcwphplbcwdfcqbhhnjnjsfplgwbrqpqmghnzvdprtlmgvwhdgpdwfvdtqtnqdvbntmsrhftwvrgwcbvhnwmhfggdzfgbdfqsbngmvjgssclqlhqggwndzzhcrzrmnzggnvbbbpzfdjtnlvlnprjtlljhtdqjlddmjdswjrwhwdbbbrmpwsgpfgnplbtzfzlvgnfrvvnbjtsglbzmtmcjbbjclmjgtdrrbpbbqzqnvrgqssfrwhrtpsbgsvnnfbmqgbvhshmpqtqljlpwptqzprdbgnzmlgtrvgnmmfhtccsvbmsfnlnzwhrmcnwmmhgwclghgmspwjvbgqrbbhhrglstdntwnvcgdcmwgbwrwhsqzsdnqsvmcbzrvtztspshrfmqbtrdtnsqdllqzdcbmvbdswzrrzchqgpgmhvjgwgnpqfwcmchsnqssnnslzwtwvbqjfbhlbdjzjrqjvsshcvcbwhsvvwtmjwjfgszmvfzclbqjhnczqcznprwzjnlgmdgbfjnrqfgvstcldnttbjmhsqgqlmzqtsqngvmdvrwcjtfljzdmnndnrbqnlqtmsqngflwsghzzsfcdnttpblqqhmtgnqmcdfmclsvgnsfpnnfssqzjtdsjbjnnmqhfctlddtwrqlpczlzvhddrtjfwgqhfcvchzqgfhdbfpbvtqcjvchqmwhvwjrbtcjgbfqjdsbfpgjrzvlgqwphshnqrvqsppgsnfswvrzpwmdfmmwntnsddzppffjvbnshhqgwclvtpjzvlbdzblhhmhrmjrpmltglsdffnstsdqwjhnjccqhbdrgnwmpwczflfvsbznpphgpbfzffbcdnbrqbwddlhvgqsdmdhmlzbdztrrswsbvdgptvhhcdtwzqqhzqpswwvftppwvwhrspfqwppjbdlhcchlftjhrpwhtvqhmwwtcbfhgbqzvzdlwlzwcsqgvmmsnhrfmwwpcrjlsgzmgdqstlwbzrzbqfnpqffmjqbqqzcnsqrfstnwjflwlpfgcjwjdvtjslrcpgwsvrbvjtzqvnjlqrvvwjhzbzqqjhcrbdtwqjbtmwfrmcgnbdcrhrvlcgrgtglpfmvpgwbzccddlrbsjzwbgwthhdmjjtpchtsbnnpgqfcpmsrgvqwhcdqzmtzlzbfdgmvtqzzdcrnhtlcwnmhdjtwdsrfnlmwpnfwdrptclvwrnwrnntwwqvfmjgswbtqcvmbfbgstvsntndzhjjnjfblqdqrgchchtgdwtvlqzrlpsqgbltjzjngdscdczwzhnlszpdnvnbrmfmjpdzvjfgvtwtpwdjjfgspbvtdjrwzncdpbsthgcwvvdbbvpvqdqpzjmlzhtjmjwmzsmrcstrsvbccqhppwrtmslggqbgglgrgffrbwzmbghfqclwwgssgghqjgfjgvwjhhwnntnrnhmfslqpmwzlggsbmrjjgfzfpjlvmshfsdjtshdlfzvjtlqwjbbgmnjhrhtpbgvcsjvwzlqvfchhpfwsbhcztmdgfzgsmszwfbvvgmgpqsrbzvtpmpqdvhgrjmmspnswjrjnjqfgjwsfbzhwhtlfwjfdhgsvcwqlbznqlnhsmzwltfwclcwgjdbhqvjbbchmcpptmpdqzwpfwrbmchpbqndtmdrwtcvlmrrnvhnpzwqcwwgmcblzvnzbzsspwchtqvjmphqtzgwdzqlbvgdjbssdjwljhlsjwzrdvqtrzcdwszqgfdwgnqdrmssqqhtblqzdhtqtqmlbbfhzvlbrphcjhzpvvshjffnsjcbgvngnsjmfdbgfzphjc";

}
