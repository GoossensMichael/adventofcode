package git.goossensmichael;

import git.goossensmichael.utils.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Day21 {

    private static final Logger LOGGER = Logger.getLogger(Day21.class.getName());

    private static final Set<Character> OPERATIONS = Set.of('+', '-', '*', '/');
    public static final String ROOT = "root";
    public static final String HUMN = "humn";

    private static long part1(final String[] input) {
        final Map<String, Operation> yells = parse(input);

        return yells.get(ROOT).execute(yells);
    }

    private static Map<String, Operation> parse(final String[] input) {
        return Arrays.stream(input)
                .map(Day21::toOperation)
                .collect(Collectors.toMap(Pair::left, Pair::right));
    }

    private static Pair<String, Operation> toOperation(final String declaration) {
        final String[] parts = declaration.split(": ");
        final String name = parts[0];

        final Operation operation;
        if (parts[1].length() > 5 && OPERATIONS.contains(parts[1].charAt(5))) {
            operation = new Aritmetic(parts[1].substring(0, 4), parts[1].substring(7), parts[1].charAt(5));
        } else {
            operation = new Number(Long.parseLong(parts[1]));
        }

        return new Pair<>(name, operation);
    }


    private interface Operation {
        long execute(Map<String, Operation> helpers);

        PartialSolution solve(Map<String, Operation> helpers);
    }

    private static abstract class PartialSolution {

    }

    private static class CompleteSolution extends PartialSolution {
        final long value;

        public CompleteSolution(final long value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }

    private static class MissingNumberSolution extends PartialSolution {

        @Override
        public String toString() {
            return "x";
        }
    }

    private static class IncompleteSolution extends PartialSolution {

        private final PartialSolution left;
        private final PartialSolution right;
        private final char op;

        public IncompleteSolution(final PartialSolution left, final PartialSolution right, final char op) {

            this.left = left;
            this.right = right;
            this.op = op;
        }

        @Override
        public String toString() {
            return String.format("(%s %s %s)", left, op, right);
        }
    }

    private static class Aritmetic implements Operation {

        private final String left;
        private final String right;
        private final char op;

        public Aritmetic(final String left, final String right, final char op) {
            this.left = left;
            this.right = right;
            this.op = op;
        }

        @Override
        public long execute(final Map<String, Operation> helpers) {
            return switch (op) {
                case '+' -> helpers.get(left).execute(helpers) + helpers.get(right).execute(helpers);
                case '-' -> helpers.get(left).execute(helpers) - helpers.get(right).execute(helpers);
                case '*' -> helpers.get(left).execute(helpers) * helpers.get(right).execute(helpers);
                case '/' -> helpers.get(left).execute(helpers) / helpers.get(right).execute(helpers);
                default -> throw new UnsupportedOperationException();
            };
        }

        @Override
        public PartialSolution solve(final Map<String, Operation> helpers) {
            final Operation left = helpers.get(this.left);
            final Operation right = helpers.get(this.right);

            final PartialSolution leftSolution;
            if (left == null) {
                leftSolution = new MissingNumberSolution();
            } else {
                leftSolution = left.solve(helpers);
            }

            final PartialSolution rightSolution;
            if (right == null) {
                rightSolution = new MissingNumberSolution();
            } else {
                rightSolution = right.solve(helpers);
            }

            final PartialSolution partialSolution;
            if (leftSolution instanceof CompleteSolution leftCompleteSolution &&
                    rightSolution instanceof CompleteSolution rightCompleteSolution){
                partialSolution = new CompleteSolution(solve(leftCompleteSolution, rightCompleteSolution));
            } else {
                partialSolution = new IncompleteSolution(leftSolution, rightSolution, op);
            }

            return partialSolution;
        }

        private long solve(final CompleteSolution left, final CompleteSolution right) {
            return switch (op) {
                case '+' -> left.value + right.value;
                case '-' -> left.value - right.value;
                case '*' -> left.value * right.value;
                case '/' -> left.value / right.value;
                default -> throw new UnsupportedOperationException();
            };
        }

    }

    private static class Number implements Operation {

        private final long value;

        public Number(final long value) {
            this.value = value;
        }

        @Override
        public long execute(final Map<String, Operation> helpers) {
            return value;
        }

        @Override
        public PartialSolution solve(final Map<String, Operation> helpers) {
            return new CompleteSolution(value);
        }
    }

    private static long part2(final String[] input) {
        final Map<String, Operation> yells = parse(input);

        // It is given in the assignment that "root" is an aritmetic.
        final Aritmetic rootYell = (Aritmetic) yells.remove(ROOT);
        // It is given that "humn"is a Number.
        final Number humnYell = (Number) yells.remove(HUMN);

        final PartialSolution left = yells.get(rootYell.left).solve(yells);
        final PartialSolution right = yells.get(rootYell.right).solve(yells);

        final long x = resolve(left, right);

        LOGGER.log(Level.INFO, "Solve by hand if you would like to:");
        LOGGER.log(Level.INFO, String.format("%s = %s", left, right));
        return x;
    }

    public static long resolve(final PartialSolution left, final PartialSolution right) {
        final long x;
        if (left instanceof CompleteSolution leftCompleteSolution && right instanceof IncompleteSolution rightIncompleteSolution) {
            x = resolve(rightIncompleteSolution.left, rightIncompleteSolution.right, rightIncompleteSolution.op, leftCompleteSolution.value);
        } else if (right instanceof CompleteSolution rightCompleteSolution && left instanceof IncompleteSolution leftIncompleteSolution){
            x = resolve(leftIncompleteSolution.left, leftIncompleteSolution.right, leftIncompleteSolution.op, rightCompleteSolution.value);
        } else {
            throw new IllegalArgumentException();
        }

        return x;
    }

    public static long resolve(final PartialSolution left, final PartialSolution right, final char op, final long acc) {
        final long x;
        if (left instanceof CompleteSolution leftCompleteSolution && right instanceof IncompleteSolution rightIncompleteSolution) {
            x = resolve(rightIncompleteSolution.left, rightIncompleteSolution.right, rightIncompleteSolution.op, calculateReverse(acc, leftCompleteSolution.value, op, true));
        } else if (right instanceof CompleteSolution rightCompleteSolution && left instanceof IncompleteSolution leftIncompleteSolution){
            x = resolve(leftIncompleteSolution.left, leftIncompleteSolution.right, leftIncompleteSolution.op, calculateReverse(acc, rightCompleteSolution.value, op));
        } else if (left instanceof MissingNumberSolution && right instanceof CompleteSolution rightCompleteSolution) {
            x = calculateReverse(acc, rightCompleteSolution.value, op);
        } else if (right instanceof MissingNumberSolution && left instanceof CompleteSolution leftCompleteSolution) {
            x = calculateReverse(acc, leftCompleteSolution.value, op);
        } else {
            throw new IllegalArgumentException();
        }

        return x;
    }

    private static long calculateReverse(final long base, final long value, final char op) {
        return calculateReverse(base, value, op, false);
    }

    /*
     * The leftComplete is needed as for - and / the base and value need to flip places.
     * e.g.:
     * 1. 20 / x = 10 -> x = 20 / 10
     * 2. 10 - x = 5  -> x = 10 - 5
     *
     * For all other cases where x (the "unknown" or "incomplete" side) is on the left side or the operation is an
     * addition or multiplication the calculation order remains the same: x = base (op) value.
     */
    private static long calculateReverse(final long base, final long value, final char op, final boolean leftComplete) {
        final long newBase;
        if (leftComplete && (op == '-' || op == '/')) {
            newBase = switch (op) {
                case '-' -> value - base;
                case '/' -> value / base;
                default -> throw new UnsupportedOperationException();
            };
        } else {
            newBase = switch (op) {
                case '+' -> Math.subtractExact(base, value);
                case '-' -> Math.addExact(base, value);
                case '*' -> Math.divideExact(base, value);
                case '/' -> Math.multiplyExact(base, value);
                default -> throw new UnsupportedOperationException();
            };
        }

        return newBase;
    }

    public static void main(final String[] args) {

        // Parsing input
        final var testInput = TST_INPUT.split("\n");
        final var input = INPUT.split("\n");

        {
            final var expectedResult = 152L;
            final var part1 = part1(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 1: %d", part1));

            if (expectedResult == part1) {
                final var result = part1(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 1: %d", result));
            }
        }

        {
            final var expectedResult = 301;
            final var testResult = part2(testInput);
            LOGGER.log(Level.INFO, () -> String.format("Test part 2: %d", testResult));

            if (expectedResult == testResult) {
                final var result = part2(input);
                LOGGER.log(Level.INFO, () -> String.format("Part 2: %d", result));
            }
        }
    }
    private static final String TST_INPUT = """
            root: pppw + sjmn
            dbpl: 5
            cczh: sllz + lgvd
            zczc: 2
            ptdq: humn - dvpt
            dvpt: 3
            lfqf: 4
            humn: 5
            ljgn: 2
            sjmn: drzm * dbpl
            sllz: 4
            pppw: cczh / lfqf
            lgvd: ljgn * ptdq
            drzm: hmdt - zczc
            hmdt: 32
            """;

    private static final String INPUT = """
            tsnz: 2
            hrfv: smns - nfng
            wfhj: 2
            rlqf: 3
            pngc: vdvn + qtgs
            hmtl: 3
            jdtd: 8
            vgvd: 2
            gmnw: tlgd - ddzn
            bnpd: gwfw + gscj
            fpjq: 3
            vdlz: ldbn + snzt
            vbbn: 8
            qhln: 14
            zlsh: 1
            fbqb: lldv + tmjt
            bgzv: 2
            hgsl: 2
            ffrl: hbqz + hntc
            bzvc: vqgz - smth
            vnbc: nwwm + jgjc
            humn: 2906
            gpsg: fqvb * mgjd
            gqrv: llwb + dmmt
            nrdl: 5
            tnsc: ttvc * mdwt
            hhfl: zhhl + flmf
            rgzn: glpv + sgjf
            bvsz: 2
            cfsn: 2
            pthd: tnmf * zdfh
            pnfz: wqhs * rhhz
            vrjs: 4
            cdsv: jbtg + flbv
            gzgv: mgcv + tjbj
            cntt: hspg * cdjb
            cngl: qvct * rwnf
            zlvt: dwgs / vdrj
            zjhr: 2
            snpf: bsdr * plqn
            qvwm: 3
            sjqr: 6
            dclg: 4
            pzqq: htbt + fwzw
            lpll: 15
            tgvz: 16
            ldtb: wsnp - tbfp
            tjcf: wvcr / pjzb
            sjgr: 3
            hthb: 3
            dnhg: mnvs / wcvd
            cdfp: jvqz * vlmb
            djfl: jdvn * pqcz
            mhvw: zrqd + vvdm
            zmsf: jqtt * jwbg
            hswt: 1
            thgv: 2
            vnmq: lgrn + ggst
            zhmr: 1
            tvbh: nwmr + wshw
            vdcv: mvsl + mcsn
            nmbj: 3
            mrfp: 2
            wdwr: cffj - fhgj
            bpzt: 7
            ltgr: 2
            qqhj: 12
            tljh: tvvh * qwll
            bnbt: mgwn * mqmn
            vppf: hflt * pqmd
            dqlr: zzsr * lzqb
            ctdm: 2
            vqgz: dzfv / qhtn
            wlnq: 4
            lzqb: cblv - tqhf
            dmgs: frjn * jvsb
            rsvc: 2
            llqh: 4
            mlzq: 12
            nnvb: 4
            bwnd: qvld / rdgg
            gmlr: dvgd * mjgc
            lzvh: 9
            jqrj: 5
            mcdw: 5
            jhwl: swvn * tvgn
            dgvr: ggbb * hftm
            rvps: pjgq * rcfj
            vnsz: 2
            ptdp: dhhj * grcr
            bcdw: 13
            thrn: 5
            ljwm: 5
            hwff: 8
            zmgm: 2
            mqcr: tmvb * vjdl
            bbll: gnfq * qjgn
            dvgd: 12
            dwnn: dprq * qllt
            gbtm: 3
            stcg: 7
            hfjr: jwqz * tpdj
            jzph: 2
            gjvw: 9
            tzqc: vmvt + plns
            lpvd: 6
            ncwt: 3
            fpjv: vvvg + rdvm
            cppz: 3
            cvpm: sbjl + lwzj
            gmjj: 19
            lvzp: jfbp * vhbj
            wrjd: bdsq * fpjq
            dsgb: wrln * cfvz
            cpzd: jbgs + tsnz
            bjhw: qhlz * jlzr
            mzgv: vjwj + ddzj
            njzd: nqwh / lsqj
            szqn: scld + gpmd
            grcf: 6
            dwhs: phpv * pqrf
            rsqm: 2
            phph: 2
            gsch: 8
            prmq: fcdn * vrjs
            qfpf: sffn / dbqv
            pqvp: 2
            nzdw: dflp * cwvd
            wvjf: ggzg - rnqm
            cndj: pngc * sctr
            sncp: ccvd + gnpg
            fpgb: tnsc + tpqn
            zrqd: ccvj + ztlj
            fvvn: 2
            wlgd: 5
            nwgv: jjcs * tfjv
            lwwz: 3
            tmsh: gqrv + rdtl
            djcm: 1
            qqhl: 3
            rvbc: 5
            grrv: nlrq + npfl
            ppsl: twlj + lwrl
            zjsj: fjlr + dqlr
            zpwd: tsdd + tbpn
            mpln: mvjg * vrgj
            fjbr: hrcd * rgrb
            mzvz: qppg * fdsf
            gfng: 2
            gszj: dfjb + llfc
            zvpd: mzwb + rvps
            dmbf: znzh / jvpl
            pwlc: dmgs - ltbw
            jjzd: lvlg / vmdq
            dbvq: jjzd - prtp
            hflt: 2
            pmff: 2
            dhmt: 5
            shbm: 2
            dhnm: 11
            lppn: 4
            zrtm: bzzm * pvmw
            glnt: 5
            gwfw: lzzq + bbrz
            zbqv: jvlw * gvjd
            sgdh: jtbg / wtdm
            prpb: 3
            tndt: 5
            qhct: pjsw + mmwv
            qnzc: 3
            gngd: gjtv + pwtd
            thht: 4
            bcjl: 5
            rhcc: 2
            zsjp: vshj + wqdv
            tctm: vnbp * rpnz
            mlpv: hwbs - wmsj
            twvh: 3
            jggt: tcdm * blch
            pgbf: djwl * jnvb
            tbfp: 3
            gzwf: 4
            mrfl: bgct + ljhn
            jqpc: zhfv + zjcf
            zprb: hfjr + szcz
            flmf: thht * fbdc
            znjr: 2
            rwwm: 3
            fpmz: zrpz + frzn
            cdmz: 4
            qvlv: gfwh + ggzj
            jmlr: 12
            rfsb: jtsf * dhdp
            tfjv: 3
            ndvn: hdgm * tnsp
            lclw: bbtg + qqdg
            tfdg: brln + jwlm
            jzhs: 3
            prtw: 3
            qwlr: 4
            sczz: wzls * gwmt
            smth: bnpd * phdc
            gwmt: 5
            mlqw: hvtz + gcmd
            fchm: humn - fspz
            lvdd: 2
            lmmj: wssz * jzhs
            llmf: jvnn * sjlh
            fnsp: 16
            wmrp: rsvc * rqws
            spjr: 5
            pqmd: 3
            sgdg: 3
            cdjn: zlvt - dsqj
            nvwv: 4
            bbrm: hzlt * fnsp
            dncm: 2
            gvbm: 9
            bptj: mtzt + fmbq
            qbqz: jrmb + mqcr
            lgwd: sdhf * smsc
            nhch: lzjb + zqcj
            frjn: jdgz / bnhz
            jpth: 4
            jdhj: 9
            chgb: jrvr * zqnz
            ncqp: cczj + djwg
            ngwj: 2
            tgts: hqsf * llpp
            htbt: 9
            tlgd: nvlb * hjnq
            phdc: pgwd * qsrl
            qrfq: 12
            ddqj: 2
            nrmz: jzwc * nltb
            rvch: nslv + mlzq
            qgnm: 5
            rvcc: 3
            bggd: 1
            rgvf: 3
            pvmw: 2
            ggbb: 2
            rsvr: 7
            srjw: ptbj + mdlj
            llfc: 16
            ncwb: 18
            zlzc: 2
            wlpf: 2
            vqbr: tgtm + rlrm
            vjlc: dzft - cjbr
            mvcc: 18
            fqgf: 3
            mgcw: 2
            gvqs: 20
            cqrb: jlzj * ldgf
            mgjd: 3
            fsrh: 3
            fqhq: 2
            mjjv: 9
            tjwd: fpgb + jggt
            vqtr: 5
            mwtq: 2
            wcvd: 3
            rzsf: vhrd + dplg
            szfn: 4
            ltsz: 2
            lsqj: 2
            bfpt: 13
            btvz: 5
            mcpq: gfng * sbng
            qhtn: 2
            lbhp: 2
            dbqv: 2
            swvn: 2
            phpv: rgzn * bvdc
            mwjg: 1
            srmj: 3
            srch: nqsr + bnqv
            nfds: 2
            hsdn: lzwq + gmjg
            cjbr: 2
            rmzm: 3
            hgsb: bsjl - zlsh
            sfpd: 2
            qdgf: hznr * lvdd
            fmfj: mqww + bmwh
            nhtm: pqhz / tjlc
            fhhd: 4
            cjdg: 2
            hnbg: 5
            rrvd: jqmj + hvfn
            hdbp: 4
            jcwt: nqrp - msth
            lzwq: zbjp + bqbr
            nfhl: 2
            flbl: qmwt + btvz
            fpgp: 3
            jtqj: 5
            mdlj: ccfh + gwrw
            sflj: nbvb * gjmv
            lfms: fcvp + rpgm
            tfln: tvbr * mchw
            pstz: scbt * cclm
            pvpl: vfjc / qgng
            nqcl: pqvp + wjdd
            jcrg: 7
            ftzt: lpqf * plmd
            zqmj: 2
            ldgf: 3
            zrns: zjsj / ldtb
            ztlj: 15
            ppmn: 8
            btdm: qsbn * zzml
            lmmh: 13
            gcqv: 3
            dbcq: lhrr * jmwz
            tcqc: 2
            drmd: 11
            mdcn: ppfr + vbbp
            mwtw: 2
            hhnv: 2
            gfmw: 2
            htrv: qfmt * gqzc
            pzvn: 3
            ldgc: lhfj * sqtg
            wmsj: 1
            zlrf: ndhv * pgbf
            cvbm: sdrq * wmnf
            szcz: 2
            svht: nlsq * lnzg
            hzgt: 5
            pmqf: 4
            bjsm: lmdl + lghh
            wcvv: grft + zcnw
            prvf: 13
            rmmb: 5
            hwlv: 5
            fnlp: dtjn * zvll
            pzms: 4
            ncct: hqjm + ncrp
            lpfw: znjg + mglg
            plbl: hhhz * wczz
            nfng: grgr / rtrc
            vmvt: wrlq + cntt
            jrtw: 7
            zjcf: fjhz + tcdt
            ldgq: 16
            tnzz: 2
            cmpz: mrmj - pdmd
            lqts: hqsr / tcvf
            mgfw: gdgw - ddtw
            pfzv: ppsl * jpmm
            cghd: 5
            wgcs: 3
            sgnh: 4
            qbzt: mprm - pgrt
            nwcj: 3
            zcpn: smjh * npbh
            zbfr: jcwt * fsfs
            dcfv: zsng + fmfp
            zczb: mvlb + zrtm
            ltsj: 9
            fhpr: bcsr + chvj
            fhcp: 17
            srll: 4
            sqzv: 5
            rnbp: jpwc * dgbd
            jtfc: 4
            ppsh: sfng * bsjn
            nrdf: 2
            fdhh: 3
            qlpz: 4
            gvwj: dqgj * vmtv
            plqn: 19
            tvdn: 2
            bnnf: 4
            bsdr: 2
            qbqh: 5
            brdh: zvjz + wmzb
            hjcd: qfwq + mbtr
            mrwd: 4
            nqsr: rllf * wrnm
            zvrl: rcwl * rcbn
            vjwj: clqz / twvh
            brct: 3
            zjjw: sjgr * fbzw
            gmrr: qsqq * nnvb
            twnv: 3
            cvfh: tzjw * vjlc
            jmbw: pmff * rhqs
            bbfr: 4
            fcgn: sgvz + plbl
            gnfq: 3
            hnph: wvfg + vrfj
            njgc: 2
            pmwl: 2
            ztfs: 2
            cbwd: lppj * gsch
            zgpt: jtzc / vbbn
            vsjb: 2
            ptqm: 3
            lfzg: 2
            phft: grnz * hswd
            vczf: 2
            tmjt: 2
            psbz: 9
            sgvz: 5
            trhm: dqcj * szvj
            zfnq: cbzv * rvch
            wjvh: vvml * fdhh
            jdqd: tldf + bnnj
            qwtr: ppmn * sqbc
            zdfm: 10
            fmbq: 4
            bpwg: 2
            cqmb: 1
            zpwc: sgdh * lmmh
            rjcw: 5
            wfnh: 9
            btqz: qrhp * lqts
            rczr: 3
            llsf: 2
            wllz: 2
            hswd: nrdl * tnzz
            pswf: 5
            jpwc: gqpp * twbm
            fjzt: slqb * jrvd
            pvww: 5
            qmnt: rbdl * fnhf
            qwvg: zpwc + zqdh
            gdgw: whgc + btwc
            vmzb: 2
            csvj: 19
            ggqb: 1
            zscp: 6
            mrdj: nrhc + jjmt
            gnpg: 20
            gjmv: vbdm / lppn
            gcmd: sdfb * hczv
            czwt: 3
            wfpd: 2
            lrnc: mhmb * vtgw
            hmvv: 11
            qsbn: 5
            nvds: pzpg + dbvq
            wbzd: 3
            sppw: rpcz * gtqq
            wmzb: pftp + ndlz
            wldr: cmpz * nsjv
            pzlq: fjzt + tjwd
            vdhd: phrp * djln
            sgjs: vzsp - fjbr
            lzpg: qwtz * nrmz
            bbtg: zcwj + wzmd
            svzm: 14
            vtbj: vblq + rvgc
            hzdb: 3
            scld: 2
            mghq: 2
            hzvd: vqcr + tcpw
            qtwm: 20
            zjhn: 11
            rtnc: 9
            tfsg: gfmw * dcvl
            scnw: 2
            mrhl: cltl + ggpz
            fcbf: tmsh / qzhh
            mlfn: bdnf * hmtl
            rcmz: 4
            zpqp: 2
            chls: 2
            rzns: 2
            frbh: mgfw / dncm
            vsmn: 2
            plmd: 3
            bnwg: hnhd * hnbg
            jdvn: 5
            whjg: vpdp + cpzd
            wntf: bccq + ttmp
            zbgs: 3
            nfts: 16
            tdfs: 9
            tjsl: 2
            mwpz: tlrn + wddr
            tbpn: 4
            vhrd: prtw * gpsg
            rgnc: 6
            grcs: qmwl / vspg
            tsdd: jrjp + phqn
            mbtr: 4
            whnf: 20
            hrgz: qnhp + sjqr
            zhdz: fwss * mlfb
            lhcc: 5
            thrw: hwff * dstj
            tnwd: llzt / hcrf
            qsgl: 2
            sjfm: 9
            ddzn: 7
            zdcc: 2
            prtp: 5
            rpvv: 2
            lfvv: 11
            pcjn: 3
            dzfv: qrvj + zrns
            qsqq: 4
            twlb: 19
            grsp: lpfw + cbwd
            qfmt: 2
            plhh: wqcg * gtwn
            tnmf: 4
            fmfp: 3
            hrbj: 6
            jwlm: 17
            gmjg: snwr - zsdn
            jpqg: 2
            djlr: 5
            lgpq: qhcd * rvbc
            qsll: swjw * mwsf
            glcq: lmnn / tccp
            cqzp: cnvf * vbbs
            hvfn: tmlp * nrsb
            nslv: 5
            cgqr: 3
            jnff: 2
            ccvd: 9
            sgqs: 2
            vnlt: 2
            nrhc: zbfr + rmqc
            hwtr: 3
            vlfc: 7
            rqwp: 1
            wjdd: vnsz * sbrr
            gsps: wntf + svzm
            jllb: 2
            zzsr: hgsl * tsvp
            ggzg: jrtw * ptss
            bccq: 1
            fngd: 14
            gtqt: 19
            hpbd: 6
            gtgj: 2
            nwhf: nhdw * pmwl
            dpwb: 2
            nhrp: mfjw * gbfl
            hlpm: vrsh + zdhc
            wnfb: lfnh + hwbq
            drtf: 3
            gtzf: 2
            tmvb: dznp * nfjf
            twbw: 3
            fcdn: 4
            mrjl: 4
            zhhh: 4
            htsg: tpfw / lmmj
            sffn: hbcj * sncp
            dddl: 2
            mzdz: tvbh + tmcr
            nbvb: rtnc + wdmc
            vsdp: lwwz * mgcw
            cbzl: gbdb + fdrz
            pgwd: 3
            qzjs: 3
            lqcf: grrv * zfbp
            dqln: rvjn * wblv
            qdgn: pdsm * fnlp
            qppg: 2
            ccfh: gmrr - fddz
            hmzw: wlqg + qnwr
            tgtm: fsqw * rzns
            vvhb: 2
            jbtg: mvvj + nvds
            qpqz: fvwz + gqnt
            vpjl: 6
            ddts: fcbf * wwrg
            jcwm: vldd / qsgl
            msjf: vdcv - ngtp
            pdmg: 9
            ncmd: 3
            nhfn: 4
            zsdn: rlmd / jzzf
            mvdv: tgts + gzdv
            gfdp: pmjz / rsgc
            jgbd: 3
            fqpd: 2
            bpnc: 2
            zsrw: glwz + qtjv
            zmvq: zqmh * rpth
            tbls: 2
            wmhn: 20
            bbdw: tzsc + mjlg
            jhmb: 7
            zgnm: ctbb * bqzs
            nsdq: 5
            rhqs: 3
            gmqd: 11
            twmp: 18
            dgbj: 1
            nlcw: hmmj + ssfg
            zgbn: 4
            vtvb: crdf + jczt
            mvls: zjhr * qznl
            bjhz: 2
            lgsh: 2
            hwrm: nzdw * jfvl
            ppqg: cjpb * gdrr
            cfsp: wrjd * sljm
            pgvc: jwsg * bdbt
            ppnn: 4
            hntc: gszj * bwqc
            mglg: rhbl * rrwd
            gwzg: vpph * mwhb
            ndhc: 2
            qzpj: 2
            qfwq: 3
            sjqv: twqw + nccc
            bcjn: hnnt * pdwr
            rfnq: 2
            fvld: hvvc * fqpg
            lncn: wscd + vmbv
            sdww: rgnc + pzrl
            vqbp: 3
            psmc: 2
            lvlg: nnff + csdd
            hnrm: wjvh - frbh
            cwzf: 2
            lhmg: 16
            jgjc: gtgs + wgjd
            dfjb: dfrz + mtts
            tgmh: gmlr - fvmq
            hcrj: hmvv * sbpf
            wshw: 2
            jwll: hssq - zgbn
            wrln: 2
            wcws: 2
            jffs: tndt * rwzq
            jdlb: nsbp * rbjd
            rwzn: 2
            fwzw: 2
            mthn: 3
            ftrj: qsll / vvhb
            ttfq: 8
            tnjj: qmfq * dsrb
            vdnr: 6
            vsdz: 3
            zqvn: 5
            bsss: 4
            fcts: tcqc * bmpt
            dzdd: 5
            gjjw: 3
            brqs: cgqr + mqnz
            dglf: 16
            rpjc: 2
            nwgg: 2
            hphf: 8
            dshg: 2
            bmgg: jbns * ssrr
            jrmb: hphf * hcrq
            ghsg: 3
            mwgd: 3
            wdmp: gcgw + sdww
            tldf: hcvc + hfrb
            fjhz: qhdg + lgpq
            nhzs: ssbn - pfqg
            dznp: stpm + mzvs
            bllq: 2
            gdvt: qhct * zjqf
            wqcj: 3
            wgjd: spjr * vrjj
            lvnh: 2
            bnff: 11
            gvbg: zwnm + vppf
            ztnw: 5
            qzrc: zpnw + wtgt
            qprg: 2
            ttvc: dcfv - cfhv
            mtbg: 8
            mccf: 2
            wvfg: vgvd + hbqv
            fzdr: tcfc / htrv
            jnsz: 3
            njnn: 3
            wtgt: dtdw * cfqn
            qwll: 6
            lnzg: fqgf * hzdb
            vltd: zbsc * bbfr
            zcwj: lztj * vlsw
            rrzl: 13
            lbhc: nbzg * gmqd
            qvgv: zfnq / zcsm
            cbzv: 2
            vpph: 2
            ctjz: fsjv - fztr
            rdsh: 2
            qfgs: 4
            pqsc: 3
            djwg: ldgc + wptw
            ssdc: 5
            pqwg: wpql * vptr
            bhqh: qgsh + gtpb
            bchq: lpqp * fwpp
            nrdn: 3
            tpdl: gcqv * gngd
            pgrt: 2
            sfvc: 5
            fdrz: 4
            dsqj: hhhl + wnfb
            ggzj: qfpf + pthd
            cltl: 11
            tcfc: pfzv - bmgg
            rvqd: 1
            zhll: hbnn * rfsq
            ljmw: 5
            wzfq: twrv + rddp
            vzsp: hczw * chls
            bstt: 5
            cbgz: jllb * zpzf
            szjv: 3
            qlrl: 1
            fbmd: 2
            fqsm: 5
            fglf: 2
            vbqf: 5
            sctr: 2
            wrhn: 6
            lgjv: ffrl / tjsl
            cgdm: 7
            hgsh: sjpv * jhmb
            cpsb: 2
            rjhr: cwzf * pwgs
            ccvj: zsjp * jcgs
            lpqp: zqvn * bwvr
            jrvr: 2
            pvjs: 3
            gbpr: lttb + vpjl
            jwqz: 3
            jlfd: vvnn + sfpn
            lgrn: tjjh - rmmb
            lqqs: lzzf + bttm
            smns: smlb + brpt
            bqlh: 3
            bzzm: 11
            pqcl: 7
            lljz: qqhl * qvwm
            mvjf: 2
            zpff: wlgd * pqjz
            zvll: qmlh / cjdc
            rbhw: 7
            tmcr: qhqq + gwzg
            jwrj: 3
            qgng: mwmv * wfhj
            jqmj: hprb * tfsg
            tjlc: 2
            dwvq: 3
            dtwl: pmqf + sqms
            hmmj: wzfq + vdnr
            wmnp: fbfl * bnrl
            zrgc: 3
            jvsb: jjpg + hzvf
            rcnf: cmcj * ddqj
            qhqq: wlnn + rvmv
            cjpb: 2
            prqf: 14
            vwzf: vbqf + ldgq
            dqcj: gsvt + vphq
            fvsv: 2
            ftfh: ncwb * lpll
            shlq: 6
            tmlp: 5
            ffcl: 5
            fmng: pmpj + grtv
            hmbd: rgbg - gqrt
            qlvl: fbqb + tnmv
            gjwh: mdcn * fhzg
            gggc: 4
            zrsw: 5
            hnnt: chtc * stfd
            zqmh: stcg * mwpz
            lhvs: 2
            vjss: lfvv * dqdg
            wmnf: lclw + srjw
            zmtj: njnn + jdsz
            nlsq: 3
            cclm: jcwm + zhmr
            vtgw: 2
            bfvp: 3
            wshl: 2
            bnlq: 5
            zsnp: 3
            hhtr: tgds * bzvc
            jmwz: 19
            rhbl: 3
            bjrz: 9
            vgqd: rbsn - pqsc
            zsdd: trzs * ggvj
            ljbs: 2
            hnrl: prvf + mqll
            wqcg: 11
            sfpn: tcdl * cfsn
            wzdw: vsdp + ttdf
            bsjl: 8
            gscj: 14
            hjnq: 17
            sfng: 11
            tpjd: npjd + qwtr
            wjwq: tvpb * hjcd
            dwhf: hwlv * zznc
            mqww: 5
            cscs: 19
            gnth: gmfd + prfn
            qvld: tjrt + fsrp
            wmpf: lfms * ncbz
            mfzt: 2
            bwvr: 5
            rvzz: zwjt + plhh
            cvnn: vltd + qtwm
            fhfv: qnnn + bslh
            lmdl: 4
            mdqz: 16
            tdbz: 3
            hrvz: 2
            fcvp: nwhn + sczr
            nzmz: 7
            thwd: vcqv * fhcp
            tvvh: 14
            fwfg: vnlt * rczr
            cqcj: pwvr + jsqd
            lrfs: tdhd * nchn
            nrrs: gmjh + qvlv
            wssz: 4
            mmwv: hcnn * nnhl
            dfrz: bcjl * gvlr
            czlz: vprh + gqgz
            mfjw: 3
            wdjs: fvvn * nhzs
            hljw: scnw * mdlw
            bdnf: vdgg + hpdd
            hvwt: mfmh + vvdp
            vqfz: 1
            qmwl: qrjg * qbph
            pqjz: 5
            fzmh: rnpg * srll
            nccc: jhwl * zqmj
            rnqm: 4
            mdwt: 3
            phvm: gtdp * hcqp
            tjjh: qcqp * ffbs
            hfjm: 3
            chbf: nhgv + nrms
            ncbz: 5
            ddvr: 1
            mhmb: dwmw * dcjm
            nsvg: 15
            ddtw: brdh * llhf
            mwmv: 3
            rvmv: 2
            qqdb: 3
            clgq: zrsw + dwzs
            vbdm: vtvb * jdtd
            jqtt: cdsv + rpcb
            zjqf: gtqt + rnbp
            bbrz: 14
            mcgj: bjrj + tcld
            tzgg: 13
            frzn: 1
            lqmh: 3
            gtqq: fhfs - twmp
            jdsz: 4
            vspg: 4
            mbnq: 3
            wrmc: 2
            grcn: 3
            pwjt: 1
            dhdp: 7
            czjv: 3
            szsv: pqzt * hcsb
            mprm: wdrz + ljjn
            wblq: llmf / nnhg
            lqsz: blrl - jwzt
            hrjs: 4
            gsvt: 6
            tfgc: 3
            fsrf: 2
            cfrh: gffs * bnlq
            ptbj: cdhg + fjnh
            zbsc: 5
            llbt: 7
            qwjc: 4
            msdb: ctzw * lbhp
            dcjm: 2
            jbgs: mfbb * fwht
            dnjz: 9
            hcpm: 7
            dwgs: tgbf + zdsl
            twrv: vzcp + gvbm
            nrvn: rzhc + vmrd
            gpzm: hlpm * dnjz
            lzrf: jwll * jnhs
            chzq: 6
            nbzg: lwtg / qzpj
            cczj: dgjd / qwlr
            hrcd: 19
            lmnn: rssw * whjg
            vvdp: 6
            dgbd: 2
            hnhd: 5
            rltv: 2
            ngcg: bsmt * qshb
            fzdn: nwhf * fqpd
            rdgg: 2
            dtdw: 2
            mjgc: 3
            bdbt: mqjm + hcmm
            rwgg: 11
            cdcv: qvbd * jqmw
            zzsc: zqvv + dglf
            lzjb: 3
            crrb: lbhc + gpzm
            nqrp: wdmp * btzf
            wwmt: ngcg - hjln
            hcmm: scjr * wdwr
            dstm: qhln + drlw
            nltb: 3
            qcqp: wrmc * rnhn
            gcdn: htcl + hcbz
            rczp: mmrp + rsvv
            fjlr: fchm * njsj
            zcnw: 9
            twds: 2
            dflp: 2
            bccv: 6
            pdwr: 2
            dwzs: bsgl * fgjv
            hfrg: 4
            gbfl: 2
            mtzt: 3
            qmfq: wjbl * wlpf
            dhvn: bdvh * ccps
            llbs: 2
            jwsg: 6
            jlwz: 7
            hzlt: 4
            qwtq: ppld / twbw
            gqzc: 3
            qvbd: 5
            wgsj: tpjd + pptt
            rpcb: gqsf + rtsw
            jqdf: dbzd / rpvv
            vdjz: ljwm * njgc
            pgdm: mrjl + wgcs
            npwg: 3
            zgvw: pvpl - vjss
            npbh: pbzh * sgmz
            fzjd: zrcm + qncj
            tjrt: mccf * cdjn
            jbns: csvj * shbm
            mcvf: vwzf + ncqs
            hpdd: ccwd / hhnv
            wpql: psvd + pzlq
            rsvv: hhfl + mwhm
            ttrq: 2
            dbnp: lvzp * thrn
            dzwz: 3
            pqcz: 5
            dprq: rwnw * bftj
            ljhn: 5
            llpp: djlr + jzph
            dhhj: 10
            gtwn: cfrh + rvjs
            jndn: 2
            llgd: ltsz * msdb
            frnp: tljh + zsrw
            dbgc: 2
            smjh: wpbt - szqn
            qmlh: ljbs * bwch
            dphb: mwtq * gqtb
            lggw: wdlb + zvrl
            qznl: 5
            fnhf: 4
            vmrd: 10
            lztj: 2
            pzpg: 8
            rnpw: lzqq / nlwz
            pvln: 5
            mbsc: wmgn * hsgr
            lpwg: 2
            vwtd: svht * dbfq
            bmpt: 4
            fpmd: pvjs * sjtt
            vtlz: dtwl * zmgm
            nvlb: 2
            gwrw: ndcf + ljsr
            wvcr: ssdc * wvbg
            rjzt: 14
            vzrv: 2
            gtpb: wldr + qwvg
            zccz: 2
            hwwb: 5
            sbvw: 7
            sbrr: 4
            zntf: 2
            cdsl: 2
            tcvf: 4
            fdgq: dgwj * htfd
            ltbv: psmc * grcn
            mblw: wptb + wshl
            cqrp: cnsb + rgqr
            nlrq: 9
            ptss: 5
            zqvv: 3
            fdfj: bptj * mngq
            gcgw: 2
            mjlg: glcq * cjqm
            pjzb: 5
            bnrl: jnff * hmzw
            mvwb: 5
            dlbj: srch + vnmq
            wmgn: 3
            qjgp: rhcc + pdmg
            zsrq: lptf / bqzn
            vshj: 3
            zdhc: 12
            swnn: 4
            msnd: 6
            nhdw: zntf * nlcw
            wzls: bqlz + vtbj
            qnnn: vlcb * vjgg
            nsbp: jcrg + qrfq
            hwbs: mjqt + hrsl
            cjqm: 7
            prdz: zrfj + dsln
            pzzl: czlc * nmgj
            twhf: 6
            mgwn: tvdn + chgb
            ggst: 1
            dgwj: pvln * ccdj
            qwbj: 5
            cwvd: 13
            mcsn: rjhr + ndtn
            frbg: rjhc + mdzl
            bhcz: 2
            jcsf: 8
            zwjt: mlfn + hhtr
            dqgj: 3
            dhpp: zjhn * nvwv
            rcwl: 2
            mqjm: 4
            pcnn: 9
            vfch: 2
            nnhl: vdjz + tdfs
            sprb: zjjw + djdj
            nwhd: 4
            jjcs: 3
            ctjb: 4
            zbbq: fsrf * nsvg
            vmvh: phph + ppsh
            mgzp: nmbj * mrfl
            dfsn: 4
            cffj: szfn * nhfn
            ddzj: 1
            btzf: vwtd + wqnw
            fsjv: 8
            lrhm: 4
            crhb: bpbc * vhwj
            ldrv: 2
            pqwq: prnb * ghsg
            llrf: 12
            tlzg: 14
            qnml: qwbj * rltv
            ctbb: 9
            hrnb: lvnh * tgmh
            hvtz: mzbj * gzwf
            hbqv: mjjv + lmfl
            ggpz: 12
            bmch: 2
            sdjz: mspf * rswd
            wjbl: 3
            mjqt: nrvn + pzzl
            tmjg: dbgb + chzq
            rfsq: ldtw * dpwb
            zhfv: rsvr * hfrg
            fddz: 2
            grcr: pcvz + jfrl
            vmtv: ctjz + mblw
            zznc: 5
            dsln: 1
            hprb: 2
            lrqp: fngd * cdcv
            cnvf: jtfc + nfvc
            nffp: 2
            znmj: 2
            cwlb: 3
            zmbn: 2
            nqwh: bllq * ppqg
            bwcm: dhzq + dgbj
            rgqr: 20
            qqdg: znjr + bcjn
            jbpr: 7
            whcz: jtcl + zscp
            hfgs: lnjr * nrdn
            ssqt: hwts * pbwn
            nhrv: wlqp + bwsm
            cfhv: 3
            psgr: 1
            qvqf: thgv + dhnm
            vlsw: 5
            lldv: 5
            rcmn: 2
            tcdm: 19
            rzmq: 1
            dprw: 5
            nhgv: 4
            tccp: 2
            tnmv: mrwd * mzvz
            mdzl: 7
            vdzl: 5
            fbdc: 2
            dwvd: 2
            bpbc: plzd * lggw
            wmmz: 1
            qwdp: zfbv * gmjj
            gqrt: jbpr * msjf
            wrsc: 3
            phml: hwtr * dwvq
            crfq: smdq * fczq
            vgwz: sgdg * lqcf
            pbwn: 5
            jzwc: dvbw / mvjf
            mzbj: 8
            crdf: fvvp * pgdm
            wlqg: 1
            jnhs: 3
            bsww: 1
            qjgn: 3
            fspz: sppn + gccm
            fsfs: 2
            scdb: cgdj + psjm
            dbzd: gfbl * bbdw
            wvmd: tzdh * dmwv
            mlcf: 3
            dlqv: 16
            wptb: 5
            cpgj: zbbq + sprb
            tcld: jqrj * mfzt
            bhsg: ltbv + jpnj
            sbpf: pwpf / qnzc
            wtdm: 2
            ztgr: qqzh * cpgj
            nsdh: 4
            glpv: gqzl + qdgn
            scqh: 5
            rzhc: jdhj + dhtm
            mzvb: 8
            dhtm: nfts * gtgj
            mptr: whwc / chmm
            pjsw: zcgf * zngt
            qwtz: qrzb * cqzp
            cjdc: 2
            tjlr: 8
            wlnn: phvm / qmmc
            ccww: 9
            dhgn: 2
            zhhl: vzbp * fsph
            lptf: dgdm + jqdf
            hjlw: brcj + lhcc
            vqcr: 4
            jrlh: 2
            pczh: frnp - cndj
            mmsr: 1
            jvpl: 3
            jvqz: pbnq + jnml
            rllf: 2
            cfvz: dbgc * twsc
            mfmh: 7
            zcjg: 2
            stfd: 5
            hczv: 2
            mrmj: prnv * bqrg
            gqsf: drmd * brct
            gbsp: hdbp * fndn
            ddwj: 4
            zdfh: 6
            zmjf: dchs + fdfj
            qhvc: 5
            wqnw: sgjs / flbl
            gmnd: pwbw * fpgp
            nlwz: 6
            nhzn: 12
            bhhw: gjvw * pcjn
            fcjr: 2
            fztr: 2
            rlrm: dqhs * phml
            tzsc: whcz * fmvb
            rssw: 2
            clqz: cqmb + whnf
            scjr: 3
            tgbf: wwmt * ltgr
            jgnt: vqrz + hcfh
            mtts: mvjq + bdzr
            hqls: 10
            qhlz: 13
            gqgz: 5
            cbmh: hgsh * mgzp
            hczw: cqpc - ztgr
            jtsf: 2
            sppn: rcnf + rrvd
            csfd: bhrh * mbnd
            tfgn: hrnb * zdfm
            sgjf: fhhd * zsrq
            svdl: 2
            ppfr: dfsn * bhsg
            zrff: dstm + bbrm
            tzdh: 3
            ppld: nszn * szjv
            bdhg: nhrv / rfnq
            npjd: brqs * scqh
            flnz: gcpr * qlpz
            wqtd: 3
            zbct: hljw * jnsz
            jvnn: 3
            srws: 2
            qrmh: tfln - sflj
            dqtn: 5
            mnjg: wmhn * dcbt
            rdvm: hgrl * fbmd
            grtv: wvmd + qbzt
            gwgb: bsrv / mghq
            sjtt: 13
            hgrl: 5
            bdzr: mnqb + gsps
            twsc: zccz * ffsp
            spmz: sczz - zgpt
            mqmn: pcpq + lgwd
            pdsm: 2
            qgsh: vmvh - bsss
            vptr: 2
            tvgn: 3
            qqzh: wbzd * hthb
            ffnm: zbrc / rsqm
            gqtb: cvpm + schq
            hcrf: 2
            hhhl: ncmd + mptr
            rpgm: 1
            fqpg: 2
            lwrl: sbsn * cmwj
            plzd: 3
            nwzz: 4
            ncqs: zpqp * cmch
            mvjq: 20
            bslh: ndvn + qvhw
            thsh: lhrv - czwt
            zbjp: gwgb / mwsp
            wblv: 3
            nbfp: 2
            phqn: twnv * jpqg
            lmfl: hqls + mvls
            dgjd: dgrd + crhb
            mbgh: nrhz * vlfc
            dqhs: 5
            rgrb: 11
            nrhz: llbt + ztfs
            qrvm: 7
            zrfj: hrjs + wcws
            wztc: wjjg + bvsw
            bttz: 2
            vzcp: 1
            pwgs: pwlc * vdzl
            lpqf: zljz + lrnc
            vvnn: 3
            jjbn: 4
            ngtp: glml * nfds
            llhf: 2
            stpm: 6
            wnst: 3
            pwvr: 1
            flbv: vgwv - ttfq
            mvsl: spmz + whlr
            rblr: 5
            twbm: 3
            smvw: 15
            njsj: 4
            vvdm: qwdp * prqf
            rths: pqlz * shnl
            fndn: 2
            qvtz: 3
            btwc: gbpr * dmbf
            thqw: 2
            hgph: 13
            ljsr: 10
            vphq: 7
            hcnn: crrb + hzvd
            rcjm: 2
            ffss: pcgs * bscg
            jpnj: wfnh + njzd
            zzml: 2
            sbng: dlqv + smvw
            mfqg: 5
            nszn: zlrf + dqzg
            sqbc: 4
            pzrl: 3
            mnzd: 5
            sdhf: 2
            jcmp: cjgg * wnst
            qlzn: thwd - mvcc
            dlmg: frbg + zsdd
            mjbj: 15
            mnqb: 14
            vvml: htsg + tpdl
            mwsp: 2
            dzsr: rths * dprw
            dtbh: qttw * dszc
            ctqm: 4
            dnqs: 17
            gbdb: 3
            qvhw: bjbh * gvwj
            bwqc: 4
            jgdn: nqcl + dhmt
            nrmd: tjcw * cdsl
            zzjv: rbhw * mgwv
            rrpz: 7
            zvjz: zcjg * mvzs
            tlrn: zmsf * jfzs
            ztrc: 1
            pcvz: 2
            hftm: bdhg - phpz
            hspg: 3
            jczt: 3
            zngt: gmnd + bnwg
            ncrp: ztnw * tmjg
            vhwj: 3
            ldbn: ddfp + rwjv
            gzrp: 3
            jlzr: 2
            jfvl: 2
            mrvr: bhcz * ffcl
            zqdh: flwb + jnbr
            tcpw: jffs - hwrm
            gfbl: 2
            fccf: 3
            flwb: 2
            dthl: 7
            gccm: llgd * zpwd
            wdrz: sqlt + cdrg
            hcqp: 17
            lwtg: zmbn * cqcj
            nnff: mwgd + rczp
            ptzv: qmsn + rlqf
            rpth: ddts * gzgv
            ljjn: 7
            ttdf: 1
            szdc: czlz * nrdf
            hbmm: dzsr / zrgc
            lbzj: 4
            hbnn: 3
            qhtt: qqpp * pgmh
            gvlr: 7
            pjgq: qvtz * hbtr
            bdvh: wdjs - zhdz
            jzzf: 7
            wzmd: 13
            jlwd: mrvr + gdmv
            pptt: dlbj * gggc
            gzrs: 2
            hsmj: zczb * rcjm
            csfb: cngl + rrpz
            hcbz: lqqs + cbgz
            brsf: lssl + tzgg
            vsjn: lphc * nvhj
            dchs: jcmp + qdgf
            vrjj: 2
            bjbh: csfd - nrlg
            drnl: jznt / snjp
            jfbp: 2
            bqrq: czjv * bpzt
            mmgl: wjtj * sgnh
            grgr: mwhz * dtbh
            bbwz: 6
            dwmw: 4
            cfhp: 4
            djwl: 3
            twlj: mrdj / ngwj
            srnd: glnt * jgnt
            lmgj: tzqc + nrmd
            jznt: gnth * jndn
            czhj: hrfv * bccv
            ssbn: qswz / whrf
            mzwb: dlmg + vqbr
            wqmn: sdjz + rdjh
            cfqn: 5
            wrlq: lrhm * tmvq
            root: dbcq + zmvq
            dszc: 17
            gzdv: 2
            mrgs: 3
            zqnz: zlzc * wlnq
            plns: 4
            gqpp: 13
            fwwb: 3
            rvct: wzdw + psgr
            fjnh: 5
            dcbt: 5
            twqw: 17
            whlr: wrwt * mlqw
            tgds: zjdj * sdqn
            lzzq: 5
            spbb: 1
            bvsw: wrgd * mbsc
            rwnw: 2
            rpnz: bstt * fvbg
            jwzt: rjzt * rlmj
            hqsr: gnbd * hnrl
            fbfl: mvsn * mcdw
            chvj: 5
            cfwm: hsdn / bpwg
            blch: 3
            vjdl: mzgv - qlrl
            gblf: dddl + sjfm
            bnhz: bpnc + qgnm
            chtc: 3
            sbjl: 3
            tpfw: bchq + fhfv
            sgmz: 2
            gcdw: 2
            zfbv: 2
            rmqc: wblq * bjcd
            ldtw: pcnn + mrfp
            bqlz: 3
            qvvs: mdqz + tjcf
            rwnf: 10
            vggv: 3
            gjtv: pjvd + zmjf
            zpzf: 4
            qrjg: 17
            pmjz: drnl - dsmw
            bsjn: 5
            fdvc: 2
            czlc: 5
            ddfp: 4
            npfl: 2
            ndlz: vzrv * zspz
            pjhg: 3
            tnrg: 1
            vlmb: 2
            wdlb: 1
            pgmh: tfgn + lmgj
            cqds: 2
            grdf: fmng + njjs
            trzs: 3
            fhzg: 2
            wvbg: ptzv + mnzd
            pwbw: 3
            tpqn: qqhj + hjlw
            jnbr: dntw + zsnp
            vqrz: qwjc * zhhh
            smdq: 3
            fwht: vhzm + zcld
            qzhh: 2
            wcrq: 5
            zbzt: mvdv * qvsg
            bbqm: 13
            phpz: cqqw + ffnm
            swjw: 2
            djjq: hfgs + tzmw
            qhcd: 5
            dzft: vsjn - cqrp
            qsrl: 2
            sqms: bfvp + rnjb
            rdtl: prpb * nfwg
            hpct: zcvd * hwwb
            pjvd: rfqt * sjqz
            zsng: twhf + vqfz
            grft: 8
            dlvl: nwzz + gbsp
            cjgg: rvcc * bhjf
            tzjw: rsgn - lncn
            zspz: dzrg + vdpb
            cqqw: ljmw * hrgz
            nvhj: fvfz * bttz
            nrms: 2
            srwt: 5
            mfbb: 2
            vqth: mlcf + nzmz
            hwts: 17
            gcpr: tnrg + crfq
            ffll: 5
            wblz: 2
            jqmw: 5
            wrgd: 7
            qhdg: fglf * qvvs
            cgdj: 10
            rvgc: qdrw + msnd
            hsbs: lhvs * lhtz
            qnhp: ftzt + nwgg
            blws: fzjd / rvct
            frbf: dlvl / rcmn
            gtdp: 3
            tnsp: 2
            hwbq: 18
            ccdj: cbzl + dclg
            ndtn: ntcc + ptdp
            jfrl: rqwp + dlbp
            dmwr: ffll * tnwd
            hzvf: qgrh + fmfj
            zwrj: 2
            mbnd: nhzn - bggd
            ffbs: 3
            vrsh: cghd * fwlh
            pfcj: 3
            jvlw: qfgs * pfcj
            vpdp: 15
            fcvl: 4
            dqzg: 11
            szvj: 2
            nsjv: 3
            qgrh: 1
            rbjd: 4
            bjcd: 3
            spdg: jtzs + fzmh
            dsrb: 2
            brcj: 14
            tcmb: 12
            zrpz: 11
            wqdv: scgt * qbqh
            ldjc: 3
            qgcr: 7
            pwtd: rzmq + lljz
            whwc: wjwq + fcts
            schq: rwgg * zdcc
            vmdq: 2
            vtnj: 1
            wscf: bnff * wqtd
            wsnp: 9
            zrcm: qslc * qndn
            bsgl: 2
            gpmd: mzdz * vnzf
            gvjd: 2
            mngq: hfsz + wmmz
            hssq: mvwb * jlwz
            bwsm: dhvn + mhvw
            vdgg: szdc * pszf
            htlp: dhpp / stdr
            wrwt: mrhl * rqhj
            mchw: 4
            qrzb: btdm + bqrq
            scgt: 2
            bhjf: 3
            wqtw: fhpr + gfdp
            rvjn: 2
            dzrg: fflf + qzjs
            ggvj: 3
            gtgs: 5
            snjp: 2
            sqlt: 8
            pszf: 3
            whrf: 2
            phrp: 2
            cdhg: 12
            dvbw: zrff * gtzf
            lssl: 16
            djln: qprb - ncwt
            pfqg: ftfh + hbmm
            mwhm: 11
            jjpg: nwgv * lqmh
            cdrg: 10
            bmwh: 4
            cbrw: 4
            dmwv: qgcr + rngp
            jfzt: 3
            bqrg: zbzt / thqw
            twtl: dzdd * scdb
            jrjp: 8
            zjwp: 16
            chmm: 2
            lpms: dwvd * bcmf
            gprr: zbqv + hzgt
            znjg: wmrp * ldrv
            qmsn: 5
            phlv: wscf + bpjm
            rwjv: 3
            pqzt: lpvd + djcm
            sgrd: cqrb - zvpd
            ggsl: zprb * pzvn
            vjcd: gdvt * sznf
            dhzq: gqwl * vjlh
            wlqp: ldfw / hfjm
            bcsr: 2
            zgzb: 8
            jnvb: 5
            fshw: mlhp / hdmg
            slmh: hswt + jgdn
            hqsf: 5
            zpnw: jfzt * drtf
            qshb: lgjv - wmnp
            qvct: 3
            tqhf: 3
            vrgj: 3
            njjs: hgph + wmpf
            cqpc: rvzz / dggd
            qslc: npwg + ctcj
            rswd: 13
            slqb: 3
            vfjc: fzdr + nrrs
            vcqv: 5
            ccwd: rwzn * bwcm
            llwb: hlnd * ptrt
            bdsq: 3
            ndhv: 2
            rhhz: 12
            qmmc: 3
            hjvb: 4
            tcdj: 17
            vzbp: 3
            tpdj: qqlw - ztrc
            lngh: 2
            dplg: 8
            bwch: dwwh + qjvs
            ssrr: 10
            ngtq: 6
            bhrh: 3
            pqlz: wbnj * wzwd
            rnjb: 4
            sdqn: 4
            dtjn: chbf + ggqb
            wqnv: 3
            drlw: wcvv + jmlr
            gqnt: qhcz * tctm
            jdgz: nwcj * szsv
            zhzw: 2
            zwgp: 2
            bvdc: 3
            rtrc: 3
            jwbg: hsmj - vdlz
            szqq: vggv * cwlb
            ntpp: mlpv * bvht
            hcsb: 2
            fwpp: fqsm * mzvj
            vbbs: 4
            dglt: mwtw + jjbn
            dmmt: thsh * wrsc
            bpjm: grcf * rwwm
            wjjg: fzdn + gjwh
            qqlw: nsdh + fcvl
            fwss: wllz * swnn
            ztht: vsmn * bfpt
            mtrq: ffss / qhvc
            vblq: 6
            shnl: 2
            sdrq: 3
            gwbj: 7
            vnbp: 2
            jtzc: fpjv * tnjj
            wpbt: sgrd + twtl
            wdmc: flnz * sfpd
            vdvn: ddvr + rjbf
            stdr: 2
            sjtc: 2
            tvpb: 2
            jnzv: 5
            prnv: 3
            zdsl: hgsb * mcvf
            rlmd: vqth + hnrm
            hdgm: dnhg - fdgq
            ctzw: bsww + wrhn
            dqdg: bcdw * rblr
            wqhs: njhj + llrf
            zcvd: 3
            whlf: 4
            fwlh: 5
            zfbp: 3
            nwhn: 6
            rgbg: qrmh - dwhs
            dbfq: rcmz + pstz
            rcfj: 3
            scbt: 2
            prnb: 9
            zjdj: ggsl / nnth
            qjvs: cvlt / zbgs
            gdrr: 11
            hcrq: 4
            pbzh: 4
            cdjb: bmch + pqwq
            nfvc: 2
            wczz: 2
            fsph: 5
            bscg: 5
            lppj: blws + tgvz
            tsvp: lzrf / qqdb
            fdsf: cfsp + srws
            mlfb: sgqs * vnbc
            whgc: cqds * bhqh
            lhrv: rdsh * nsdq
            mlhp: sjqv * ftcp
            njhj: qjgp + hpbd
            gcdm: 2
            rjbf: zjwp * rjcw
            nrsb: 5
            brln: fcjr * ptqm
            dlbp: 5
            dvzd: brsf * gcdm
            jnml: 11
            vjlh: 12
            dntw: cjbj * dshg
            bfjm: nffp * snpf
            smqn: 4
            rnpg: wnvp + mtbg
            pftp: hsbs + vgwz
            znzh: mcpq * dqln
            mzvs: 1
            lwzj: 4
            cjbj: pwzr + sfvc
            zgnh: 3
            pcgs: vnct + wdnq
            bftj: 5
            mqll: 1
            tmvq: 4
            pwpf: gvqs + jlvr
            bvht: 3
            sjpv: 3
            pqrf: 3
            psjm: jnzv * pvww
            pqhz: lzpg / tbls
            bsrv: ttrq * pqwg
            cmch: fvsv * tcdj
            jmls: 3
            rcbn: mbnq + jnhd
            gdmv: cvnn - pqcl
            bqzn: 6
            rjhc: 4
            mtpq: 2
            cvlt: hcrj + cfhp
            vdpb: grzf + djfl
            lnjr: lrfs / jmls
            bqzs: mjbj + qrvm
            vmbv: zpff + vtlz
            vlcb: jpth + fsrh
            wddr: zcpn * czhj
            ffpr: 3
            qllt: 4
            cnsb: 1
            zwnm: ssqt + prmq
            qbph: 4
            hcvc: nhrp * bbll
            qtgs: dnqs * cdmz
            zljz: 1
            vprh: 8
            snzt: 2
            jtbg: zhzw * qvqf
            ccps: 2
            jlvr: 1
            brpt: wgsj + grdf
            bgct: 2
            sljm: 5
            wdnq: vgqd * rpjc
            nmgj: dglt + ppnn
            qqpp: 2
            hfsz: jwrj * vmzb
            nwwm: fqhq * pzqq
            cpcf: 2
            nchn: 4
            jtzs: lmvz * mtrq
            qttw: 3
            llzt: gvbg * twds
            mgcv: dhgn * gmnw
            fmcd: 12
            qncj: svdl * ffnp
            hqjm: znmj * ffpr
            bqbr: 8
            ndcf: 1
            wzwd: 3
            sjlh: qpqz * lfzg
            pdmd: qzrc + clvv
            ssfg: mtpq + hjvb
            tcdt: 4
            sjqz: 5
            gnbd: 2
            blrl: rrzl * jlfd
            lfhq: srwt * plwr
            hmzh: 7
            rbdl: 13
            fhgj: 2
            snwr: vjcd * bnbt
            ttmp: pwjt + mzvb
            nfwg: 3
            lnhj: vdhd + fshw
            qprb: vvzv * sqzv
            pwzr: 2
            rsgc: cbrw + wqcj
            jtcl: ddwj + wqnv
            qrhp: qnml - szsn
            gffs: 5
            cmwj: pgvc + wvjf
            jrvd: tfdg * fwwb
            glwz: sppw * zwgp
            lphc: 4
            tdhd: frbf + mrgs
            mzsw: spbb + hrbj
            prfn: jmbw * ltsj
            fvmq: 10
            fgjv: 4
            wjtj: 7
            hfrb: nwhd + gjzd
            ztgg: qhtt / nfhl
            szsn: 3
            vvvg: 6
            fvdc: rvqd + bbwz
            zqcj: 5
            rvjs: 18
            wnvp: qlzn * qprg
            hbcj: 2
            clvv: 3
            lttb: 1
            gqwl: cjdg * tjlr
            gjzd: 9
            hvvc: lbzj * fdvc
            nnth: 3
            dstj: 17
            tvbr: phft - cvfh
            lzzf: cpsb * slmh
            tbgt: 5
            dggd: 3
            mnmz: 10
            rtsw: nzpv - vczf
            gvnr: hrvz + vqtr
            flzm: shlq * ngtq
            ffnp: 11
            cblv: ccww * lgsh
            hdmg: 3
            mvsn: 2
            hcfh: dzwz * zwrj
            qrvj: zgnm + rzsf
            hjln: bjhz * grsp
            lfnh: wqtw + cdfp
            nrlg: cpcf * wcrq
            htfd: 5
            ftcp: 3
            rdjh: hnph + tlzg
            rngp: 4
            pmpj: lfhq + srnd
            tjbj: lngh + pswf
            ffhz: 7
            fvvp: fvld + mmsr
            dcvl: gzrs + srzh
            qndn: 2
            vhzm: gvnr * dqtn
            gmjh: dbnp + zmtj
            dwwh: 14
            fbzw: 7
            mdlw: 4
            smsc: rnpw * pjhg
            qbtg: dphb * bgzv
            mwhz: 3
            ltbw: tbgt * hvwt
            fczq: 4
            bcmf: 3
            fsrp: dmwr + qbqz
            zcgf: clgq * nhch
            sqtg: pzms + ffhz
            qtjv: cbmh + wqmn
            llvf: mzsw * tfgc
            qnwr: szqq + gbtm
            bnqv: fccf * tdbz
            rsgn: pnfz - bfjm
            mvzs: vtnj + flzm
            dgrd: jlwd * fvdc
            fqvb: 5
            qvsg: 2
            lzvr: 4
            grnz: rmzm * wztc
            srzh: 5
            jlzj: cvbm + jdlb
            hzfw: gcdw * rfsb
            ldfw: jgbd * ztgg
            fhfs: dvzd + srmj
            wptw: fpmd + mmgl
            qdrw: 1
            wscd: nbfp * bbqm
            mmrp: 4
            jpmm: 2
            rqhj: 2
            lhrr: pczh + cfwm
            fvfz: 8
            mgwv: 3
            nnhg: 2
            mwsf: phlv - fbzp
            dgdm: csfb * zzsc
            zcld: lpwg * ctqm
            zcsm: 2
            vbbp: bvsz + qwtq
            bsmt: 2
            htcl: zvcw + zbct
            msth: cppz * fcgn
            ptrt: 2
            hhhz: zzjv + mpln
            gqzl: mbgh + thrw
            tjcw: gwbj * ctjb
            bttm: 1
            jqqz: 8
            vgwv: mthn * tcmb
            nzpv: dwhf + fpmz
            fflf: jrlh * prdz
            glml: mnjg + ntpp
            vdrj: 3
            hbqz: zgvw * llvf
            pbnq: zgnh * lzvr
            grzf: wblz + bjrz
            hlnd: 4
            ctcj: 14
            rnhn: 3
            rpcz: 2
            pcpq: dwnn * ncqp
            mwhb: smqn + jqqz
            lghh: 3
            ntcc: vqbp * hcpm
            nfjf: 3
            bjrj: llsf * qwmp
            qhcz: 2
            mzvj: hzfw / llqh
            jfzs: ncct * lqsz
            fdsq: hmzh * lnhj
            rqws: bjsm + dsgb
            fvwz: 3
            gmfd: mfqg * djjq
            qwmp: 8
            rrwd: 5
            vjgg: gblf * bnnf
            smlb: qbtg * sjtc
            jjmt: qlvl + lrqp
            vrfj: bjhw * vsdz
            qmwt: 2
            fmvb: wfpd * grcs
            fbzp: vsjb * cgdm
            mvvj: hpct * cgvg
            lhfj: 3
            lmvz: 3
            csdd: twlb + jcsf
            ffsp: 3
            rwzq: sbvw + trhm
            wwrg: btqz - fwfg
            vnzf: 3
            sdfb: 3
            vvzv: 5
            lzqq: nhtm + hmbd
            mspf: 13
            qswz: bwnd + spdg
            mvjg: 4
            wrnm: 3
            qmqj: vfch * jqpc
            tzmw: 1
            dbgb: 1
            lhtz: 14
            rlmj: 3
            mnvs: dgvr + fdsq
            rfqt: 15
            rbsn: lzvh + mwjg
            tcdl: 5
            zbrc: qmqj + htlp
            vldd: fmcd * llbs
            sczr: mnmz * whlf
            gfwh: jdqd + bhhw
            mqnz: 8
            mvlb: 1
            wbnj: 3
            psvd: gprr * qvgv
            cgvg: 2
            vhbj: 4
            djdj: 2
            nwmr: zgzb + lhmg
            hznr: cscs * ndhc
            hsgr: 17
            jnhd: 8
            hrsl: 9
            fsqw: ctdm * ftrj
            jcgs: 4
            rddp: mcgj + jtqj
            sbsn: 3
            jsqd: lpms * rgvf
            bnnj: zhll + gcdn
            zvcw: 10
            vnct: dthl + qmnt
            dsmw: ztht + bqlh
            sznf: 2
            fvbg: 2
            plwr: 5
            cmcj: psbz * gzrp
            hbtr: ldjc * gjjw
            """;
}
